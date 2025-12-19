package org.sqq.registration.api.dto;

import org.sqq.registration.CooperateurStatus;
import org.sqq.registration.SouscriptionSupplementaire;

import java.time.Instant;

public class SouscriptionSupplementaireDTO {
    public Long id;
    public String prenom;
    public String nom;
    public String email;
    public Long partsSupplementaires;
    public CooperateurStatus status;
    public Instant createdAt;
    public Instant updatedAt;

    public static SouscriptionSupplementaireDTO fromSouscriptionSupplementaire(SouscriptionSupplementaire souscription) {
        var dto = new SouscriptionSupplementaireDTO();

        dto.id = souscription.id;
        dto.prenom = souscription.prenom;
        dto.nom = souscription.nom;
        dto.email = souscription.email;
        dto.partsSupplementaires = souscription.partsSupplementaires;
        dto.status = souscription.status;
        dto.createdAt = souscription.createdAt;
        dto.updatedAt = souscription.updatedAt;

        return dto;
    }
}
