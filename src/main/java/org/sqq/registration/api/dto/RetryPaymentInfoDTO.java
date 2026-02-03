package org.sqq.registration.api.dto;

import org.sqq.registration.Cooperateur;
import org.sqq.registration.SouscriptionSupplementaire;

public class RetryPaymentInfoDTO {
    public String type;
    public String uuid;
    public String prenom;
    public String nom;
    public String email;
    public Long parts;
    public Long montantTotal;

    public static RetryPaymentInfoDTO fromCooperateur(Cooperateur cooperateur) {
        RetryPaymentInfoDTO dto = new RetryPaymentInfoDTO();
        dto.type = "cooperateur";
        dto.uuid = cooperateur.uuid;
        dto.prenom = cooperateur.prenom;
        dto.nom = cooperateur.nom;
        dto.email = cooperateur.email;
        dto.parts = cooperateur.parts;
        dto.montantTotal = cooperateur.parts * 10;
        return dto;
    }

    public static RetryPaymentInfoDTO fromSouscriptionSupplementaire(SouscriptionSupplementaire souscription) {
        RetryPaymentInfoDTO dto = new RetryPaymentInfoDTO();
        dto.type = "souscription_supplementaire";
        dto.uuid = souscription.uuid;
        dto.prenom = souscription.prenom;
        dto.nom = souscription.nom;
        dto.email = souscription.email;
        dto.parts = souscription.partsSupplementaires;
        dto.montantTotal = souscription.partsSupplementaires * 10;
        return dto;
    }
}
