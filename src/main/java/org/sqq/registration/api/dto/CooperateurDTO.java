package org.sqq.registration.api.dto;

import org.sqq.registration.Cooperateur;
import org.sqq.registration.CooperateurStatus;
import org.sqq.registration.Genre;

import java.time.Instant;

public class CooperateurDTO {
    public Long id;
    public Genre genre;
    public String prenom;
    public String nom;
    public String telephone;
    public String email;
    public String adresse;
    public String ville;
    public String codePostal;
    public Boolean etudiantOuMinimasSociaux;
    public Long nombreDePersonnesDansLeFoyer;
    public Long parts;
    public Long partsDeSoutien;
    public boolean acceptationDesStatus;
    public CooperateurStatus status;
    public BinomeDTO binome;
    public Instant createdAt;
    public Instant updatedAt;

    public static CooperateurDTO fromCooperateur(Cooperateur cooperateur) {
        var dto = new CooperateurDTO();
        
        dto.id = cooperateur.id;
        dto.genre = cooperateur.genre;
        dto.prenom = cooperateur.prenom;
        dto.nom = cooperateur.nom;
        dto.telephone = cooperateur.telephone;
        dto.email = cooperateur.email;
        dto.adresse = cooperateur.adresse;
        dto.ville = cooperateur.ville;
        dto.codePostal = cooperateur.codePostal;
        dto.etudiantOuMinimasSociaux = cooperateur.etudiantOuMinimasSociaux;
        dto.nombreDePersonnesDansLeFoyer = cooperateur.nombreDePersonnesDansLeFoyer;
        dto.parts = cooperateur.parts;
        dto.partsDeSoutien = cooperateur.partsDeSoutien;
        dto.acceptationDesStatus = cooperateur.acceptationDesStatus;
        dto.status = cooperateur.status;
        dto.binome = BinomeDTO.fromBinome(cooperateur.binome);
        dto.createdAt = cooperateur.createdAt;
        dto.updatedAt = cooperateur.updatedAt;

        return dto;
    }
}
