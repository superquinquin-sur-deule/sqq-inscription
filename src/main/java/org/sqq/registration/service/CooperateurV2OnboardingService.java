package org.sqq.registration.service;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.sqq.registration.*;
import org.sqq.registration.api.dto.CreateBinomeV2Request;
import org.sqq.registration.api.dto.CreateCooperateurV2Request;
import org.sqq.registration.api.dto.CreatePairV2Request;
import org.sqq.registration.repository.CooperateurRelationshipRepository;
import org.sqq.registration.repository.CooperateurV2Repository;
import org.sqq.registration.repository.PartTransactionRepository;

import java.util.Optional;

@ApplicationScoped
public class CooperateurV2OnboardingService {

    @Inject
    CooperateurV2Repository cooperateurV2Repository;

    @Inject
    CooperateurRelationshipRepository relationshipRepository;

    @Inject
    PartTransactionRepository partTransactionRepository;

    public record OnboardingResult(
            CooperateurV2 primary,
            CooperateurV2 binome,
            boolean success,
            String errorMessage
    ) {
        public static OnboardingResult success(CooperateurV2 primary, CooperateurV2 binome) {
            return new OnboardingResult(primary, binome, true, null);
        }

        public static OnboardingResult success(CooperateurV2 primary) {
            return new OnboardingResult(primary, null, true, null);
        }

        public static OnboardingResult error(String message) {
            return new OnboardingResult(null, null, false, message);
        }
    }

    @Transactional
    public OnboardingResult createSolo(CreateCooperateurV2Request request) {
        // Check for duplicate email
        Optional<CooperateurV2> existingByEmail = cooperateurV2Repository.findByEmail(request.email);
        if (existingByEmail.isPresent()) {
            return OnboardingResult.error("Un coopérateur avec cet email existe déjà: " + request.email);
        }

        // Create the cooperateur
        CooperateurV2 cooperateur = createFromRequest(request);
        cooperateurV2Repository.persist(cooperateur);
        Log.infof("Created solo CooperateurV2 %d: %s %s", cooperateur.id, cooperateur.prenom, cooperateur.nom);

        // Create initial part transaction
        long parts = calculateParts(request.etudiantOuMinimasSociaux, request.partsDeSoutien);
        createPartTransaction(cooperateur, parts, "Inscription manuelle - membre principal");

        return OnboardingResult.success(cooperateur);
    }

    @Transactional
    public OnboardingResult createPair(CreatePairV2Request request) {
        CreateCooperateurV2Request principalReq = request.principal;
        CreateBinomeV2Request binomeReq = request.binome;

        // Check for duplicate emails
        Optional<CooperateurV2> existingPrimary = cooperateurV2Repository.findByEmail(principalReq.email);
        if (existingPrimary.isPresent()) {
            return OnboardingResult.error("Un coopérateur avec cet email existe déjà: " + principalReq.email);
        }

        Optional<CooperateurV2> existingBinome = cooperateurV2Repository.findByEmail(binomeReq.email);
        if (existingBinome.isPresent()) {
            return OnboardingResult.error("Un coopérateur avec cet email existe déjà: " + binomeReq.email);
        }

        // Create principal member
        CooperateurV2 principal = createFromRequest(principalReq);
        cooperateurV2Repository.persist(principal);
        Log.infof("Created principal CooperateurV2 %d: %s %s", principal.id, principal.prenom, principal.nom);

        // Create part transaction for principal
        long principalParts = calculateParts(principalReq.etudiantOuMinimasSociaux, principalReq.partsDeSoutien);
        createPartTransaction(principal, principalParts, "Inscription manuelle - membre principal");

        // Create binome member
        CooperateurV2 binome = createBinomeFromRequest(binomeReq, principal, principalReq);
        cooperateurV2Repository.persist(binome);
        Log.infof("Created binome CooperateurV2 %d: %s %s", binome.id, binome.prenom, binome.nom);

        // Binome gets 2 parts
        createPartTransaction(binome, 2L, "Inscription manuelle - binôme");

        // Create relationship
        CooperateurRelationship relationship = new CooperateurRelationship();
        relationship.cooperateur1 = principal;
        relationship.cooperateur2 = binome;
        relationship.relationshipType = RelationshipType.PAIR;
        relationshipRepository.persist(relationship);
        Log.infof("Created relationship between %d and %d", principal.id, binome.id);

        return OnboardingResult.success(principal, binome);
    }

    private CooperateurV2 createFromRequest(CreateCooperateurV2Request request) {
        CooperateurV2 v2 = new CooperateurV2();
        v2.genre = request.genre;
        v2.prenom = request.prenom;
        v2.nom = request.nom;
        v2.dateNaissance = request.dateNaissance;
        v2.telephone = request.telephone;
        v2.email = request.email;
        v2.adresse = request.adresse;
        v2.ville = request.ville;
        v2.codePostal = request.codePostal;
        v2.etudiantOuMinimasSociaux = request.etudiantOuMinimasSociaux;
        v2.nombreDePersonnesDansLeFoyer = request.nombreDePersonnesDansLeFoyer;
        v2.acceptationDesStatus = true; // Admin-created members accept statuses
        v2.notes = request.notes;

        // Set status (default if not provided)
        v2.paymentStatus = request.paymentStatus != null ? request.paymentStatus : PaymentStatus.PAID;
        v2.memberStatus = request.memberStatus != null ? request.memberStatus : MemberStatus.ACTIVE;

        // Mark as manual entry
        v2.source = DataSource.MANUAL_ENTRY;

        return v2;
    }

    private CooperateurV2 createBinomeFromRequest(CreateBinomeV2Request binomeReq,
                                                   CooperateurV2 principal,
                                                   CreateCooperateurV2Request principalReq) {
        CooperateurV2 v2 = new CooperateurV2();
        v2.genre = binomeReq.genre;
        v2.prenom = binomeReq.prenom;
        v2.nom = binomeReq.nom;
        v2.dateNaissance = binomeReq.dateNaissance;
        v2.telephone = binomeReq.telephone;
        v2.email = binomeReq.email;

        // Inherit address from principal if not provided
        v2.adresse = binomeReq.adresse != null && !binomeReq.adresse.isBlank()
                ? binomeReq.adresse : principal.adresse;
        v2.ville = binomeReq.ville != null && !binomeReq.ville.isBlank()
                ? binomeReq.ville : principal.ville;
        v2.codePostal = binomeReq.codePostal != null && !binomeReq.codePostal.isBlank()
                ? binomeReq.codePostal : principal.codePostal;

        // Inherit household info from principal
        v2.etudiantOuMinimasSociaux = principalReq.etudiantOuMinimasSociaux;
        v2.nombreDePersonnesDansLeFoyer = principalReq.nombreDePersonnesDansLeFoyer;
        v2.acceptationDesStatus = true;
        v2.notes = binomeReq.notes;

        // Same status as principal
        v2.paymentStatus = principal.paymentStatus;
        v2.memberStatus = principal.memberStatus;

        v2.source = DataSource.MANUAL_ENTRY;

        return v2;
    }

    private long calculateParts(Boolean etudiantOuMinimasSociaux, Long partsDeSoutien) {
        long baseParts;
        if (etudiantOuMinimasSociaux != null && etudiantOuMinimasSociaux) {
            baseParts = 1;
        } else {
            baseParts = 10;
        }

        if (partsDeSoutien != null && partsDeSoutien > 0) {
            baseParts += partsDeSoutien;
        }

        return baseParts;
    }

    private void createPartTransaction(CooperateurV2 member, long parts, String reason) {
        PartTransaction tx = new PartTransaction();
        tx.cooperateur = member;
        tx.transactionType = PartTransactionType.INITIAL_PURCHASE;
        tx.parts = parts;
        tx.reason = reason;
        partTransactionRepository.persist(tx);
    }
}
