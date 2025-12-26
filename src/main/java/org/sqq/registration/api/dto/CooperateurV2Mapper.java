package org.sqq.registration.api.dto;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.sqq.registration.CooperateurRelationship;
import org.sqq.registration.CooperateurV2;
import org.sqq.registration.repository.CooperateurRelationshipRepository;
import org.sqq.registration.repository.PartTransactionRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CooperateurV2Mapper {

    @Inject
    PartTransactionRepository partTransactionRepository;

    @Inject
    CooperateurRelationshipRepository relationshipRepository;

    public CooperateurV2DTO toDTO(CooperateurV2 entity) {
        if (entity == null) {
            return null;
        }

        CooperateurV2DTO dto = new CooperateurV2DTO();

        // Direct field mapping
        dto.id = entity.id;
        dto.genre = entity.genre;
        dto.prenom = entity.prenom;
        dto.nom = entity.nom;
        dto.dateNaissance = entity.dateNaissance;
        dto.telephone = entity.telephone;
        dto.email = entity.email;
        dto.adresse = entity.adresse;
        dto.ville = entity.ville;
        dto.codePostal = entity.codePostal;
        dto.etudiantOuMinimasSociaux = entity.etudiantOuMinimasSociaux;
        dto.nombreDePersonnesDansLeFoyer = entity.nombreDePersonnesDansLeFoyer;
        dto.paymentStatus = entity.paymentStatus;
        dto.memberStatus = entity.memberStatus;
        dto.source = entity.source;
        dto.notes = entity.notes;
        dto.createdAt = entity.createdAt;
        dto.updatedAt = entity.updatedAt;

        // Computed field: totalParts
        dto.totalParts = partTransactionRepository.computeTotalPartsById(entity.id);

        // Computed fields: memberType, partnerId, partnerName
        computeRelationshipFields(entity, dto);

        return dto;
    }

    private void computeRelationshipFields(CooperateurV2 entity, CooperateurV2DTO dto) {
        List<CooperateurRelationship> relationships = relationshipRepository.findByCooperateur(entity);

        if (relationships.isEmpty()) {
            dto.memberType = "PRINCIPAL";
            dto.partnerId = null;
            dto.partnerName = null;
            return;
        }

        CooperateurRelationship relationship = relationships.get(0);

        // Determine if this member is cooperateur2 (BINOME) or cooperateur1 (PRINCIPAL)
        boolean isCooperateur2 = relationship.cooperateur2.id.equals(entity.id);
        dto.memberType = isCooperateur2 ? "BINOME" : "PRINCIPAL";

        // Get partner info
        CooperateurV2 partner = isCooperateur2 ? relationship.cooperateur1 : relationship.cooperateur2;
        dto.partnerId = partner.id;
        dto.partnerName = partner.prenom + " " + partner.nom;
    }

    public List<CooperateurV2DTO> toDTOList(List<CooperateurV2> entities) {
        return entities.stream()
                .map(this::toDTO)
                .toList();
    }
}
