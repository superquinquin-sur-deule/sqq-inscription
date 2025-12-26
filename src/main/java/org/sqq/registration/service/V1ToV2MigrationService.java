package org.sqq.registration.service;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.sqq.registration.*;
import org.sqq.registration.repository.CooperateurRelationshipRepository;
import org.sqq.registration.repository.CooperateurV2Repository;
import org.sqq.registration.repository.PartTransactionRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class V1ToV2MigrationService {

    @Inject
    CooperateurV2Repository cooperateurV2Repository;

    @Inject
    CooperateurRelationshipRepository relationshipRepository;

    @Inject
    PartTransactionRepository partTransactionRepository;

    public record MigrationResult(
            int totalV1Records,
            int migratedMembers,
            int skippedMembers,
            int createdRelationships,
            int createdTransactions
    ) {}

    @Transactional
    public MigrationResult migrateFromV1() {
        List<Cooperateur> v1Records = Cooperateur.listAll();

        int totalV1Records = v1Records.size();
        int migratedMembers = 0;
        int skippedMembers = 0;
        int createdRelationships = 0;
        int createdTransactions = 0;

        for (Cooperateur v1 : v1Records) {
            // Check if already migrated (idempotency check by email)
            Optional<CooperateurV2> existingPrimary = cooperateurV2Repository.findByEmail(v1.email);
            if (existingPrimary.isPresent()) {
                Log.infof("Skipping already migrated cooperateur: %s (email: %s)", v1.id, v1.email);
                skippedMembers++;
                continue;
            }

            // Create primary CooperateurV2
            CooperateurV2 primary = createV2FromV1(v1);
            cooperateurV2Repository.persist(primary);
            migratedMembers++;
            Log.infof("Migrated cooperateur %d -> CooperateurV2 %d", v1.id, primary.id);

            // Create PartTransaction for primary member
            PartTransaction primaryTransaction = createPartTransaction(primary, v1);
            partTransactionRepository.persist(primaryTransaction);
            createdTransactions++;

            // Handle binome if present
            if (v1.binome != null) {
                Binome binome = v1.binome;

                // Check if binome already exists (by email if available)
                Optional<CooperateurV2> existingBinome = Optional.empty();
                if (binome.email != null && !binome.email.isBlank()) {
                    existingBinome = cooperateurV2Repository.findByEmail(binome.email);
                }

                CooperateurV2 binomeV2;
                if (existingBinome.isPresent()) {
                    binomeV2 = existingBinome.get();
                    Log.infof("Binome already exists: %s, linking to primary %d", binome.email, primary.id);
                } else {
                    binomeV2 = createV2FromBinome(binome, v1);
                    cooperateurV2Repository.persist(binomeV2);
                    migratedMembers++;
                    Log.infof("Created binome CooperateurV2 %d from V1 cooperateur %d", binomeV2.id, v1.id);

                    // Binome gets 2 parts (standard binome contribution)
                    PartTransaction binomeTransaction = createBinomePartTransaction(binomeV2);
                    partTransactionRepository.persist(binomeTransaction);
                    createdTransactions++;
                }

                // Create relationship between primary and binome
                Optional<CooperateurRelationship> existingRelationship =
                        relationshipRepository.findRelationshipBetween(primary, binomeV2);

                if (existingRelationship.isEmpty()) {
                    CooperateurRelationship relationship = new CooperateurRelationship();
                    relationship.cooperateur1 = primary;
                    relationship.cooperateur2 = binomeV2;
                    relationship.relationshipType = RelationshipType.PAIR;
                    relationshipRepository.persist(relationship);
                    createdRelationships++;
                    Log.infof("Created relationship between %d and %d", primary.id, binomeV2.id);
                }
            }
        }

        Log.infof("Migration complete. Total V1: %d, Migrated: %d, Skipped: %d, Relationships: %d, Transactions: %d",
                totalV1Records, migratedMembers, skippedMembers, createdRelationships, createdTransactions);

        return new MigrationResult(totalV1Records, migratedMembers, skippedMembers, createdRelationships, createdTransactions);
    }

    private CooperateurV2 createV2FromV1(Cooperateur v1) {
        CooperateurV2 v2 = new CooperateurV2();
        v2.genre = v1.genre;
        v2.prenom = v1.prenom;
        v2.nom = v1.nom;
        v2.telephone = v1.telephone;
        v2.email = v1.email;
        v2.adresse = v1.adresse;
        v2.ville = v1.ville;
        v2.codePostal = v1.codePostal;
        v2.etudiantOuMinimasSociaux = v1.etudiantOuMinimasSociaux;
        v2.nombreDePersonnesDansLeFoyer = v1.nombreDePersonnesDansLeFoyer;
        v2.acceptationDesStatus = v1.acceptationDesStatus;

        // Map payment and member status
        v2.paymentStatus = mapPaymentStatus(v1.status);
        v2.memberStatus = mapMemberStatus(v1.status);

        // Source tracking
        v2.source = DataSource.ONLINE_REGISTRATION;

        // Stripe session ID
        v2.stripeSessionId = v1.stripeSessionId;

        return v2;
    }

    private CooperateurV2 createV2FromBinome(Binome binome, Cooperateur primaryV1) {
        CooperateurV2 v2 = new CooperateurV2();
        v2.genre = binome.genre;
        v2.prenom = binome.prenom;
        v2.nom = binome.nom;
        v2.telephone = binome.telephone;
        v2.email = binome.email;
        v2.adresse = binome.adresse != null ? binome.adresse : primaryV1.adresse;
        v2.ville = binome.ville != null ? binome.ville : primaryV1.ville;
        v2.codePostal = binome.codePostal != null ? binome.codePostal : primaryV1.codePostal;
        v2.dateNaissance = binome.dateNaissance;

        // Inherit some fields from primary
        v2.etudiantOuMinimasSociaux = primaryV1.etudiantOuMinimasSociaux;
        v2.nombreDePersonnesDansLeFoyer = primaryV1.nombreDePersonnesDansLeFoyer;
        v2.acceptationDesStatus = true;

        // Binome payment status is "covered by partner" - treated as paid
        v2.paymentStatus = mapPaymentStatus(primaryV1.status);
        v2.memberStatus = mapMemberStatus(primaryV1.status);

        v2.source = DataSource.ONLINE_REGISTRATION;
        v2.notes = "Binôme migré depuis V1";

        return v2;
    }

    private PaymentStatus mapPaymentStatus(CooperateurStatus v1Status) {
        if (v1Status == null) {
            return PaymentStatus.PENDING;
        }
        return switch (v1Status) {
            case PAYMENT_PENDING -> PaymentStatus.PENDING;
            case PAID, PROCESSED -> PaymentStatus.PAID;
        };
    }

    private MemberStatus mapMemberStatus(CooperateurStatus v1Status) {
        if (v1Status == null) {
            return MemberStatus.PROSPECT;
        }
        return switch (v1Status) {
            case PAYMENT_PENDING, PAID -> MemberStatus.PROSPECT;
            case PROCESSED -> MemberStatus.ACTIVE;
        };
    }

    private PartTransaction createPartTransaction(CooperateurV2 member, Cooperateur v1) {
        PartTransaction tx = new PartTransaction();
        tx.cooperateur = member;
        tx.transactionType = PartTransactionType.INITIAL_PURCHASE;

        // Calculate parts from V1 logic
        long parts = 0;
        if (v1.etudiantOuMinimasSociaux != null && v1.etudiantOuMinimasSociaux) {
            parts = 1;
        } else {
            parts = 10;
        }

        // Add parts de soutien
        if (v1.partsDeSoutien != null) {
            parts += v1.partsDeSoutien;
        }

        tx.parts = parts;
        tx.reason = "Migration depuis V1";
        tx.stripeSessionId = v1.stripeSessionId;

        return tx;
    }

    private PartTransaction createBinomePartTransaction(CooperateurV2 binome) {
        PartTransaction tx = new PartTransaction();
        tx.cooperateur = binome;
        tx.transactionType = PartTransactionType.INITIAL_PURCHASE;
        tx.parts = 2L; // Binome gets 2 parts
        tx.reason = "Part binôme - Migration depuis V1";

        return tx;
    }
}
