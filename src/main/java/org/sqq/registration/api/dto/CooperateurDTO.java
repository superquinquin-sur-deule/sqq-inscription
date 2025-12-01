package org.sqq.registration.api.dto;

import org.sqq.registration.Cooperateur;
import org.sqq.registration.Genre;

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

    public CooperateurDTO(Long id, Genre genre, String prenom, String nom, String telephone, String email, String adresse, String ville, String codePostal, Boolean etudiantOuMinimasSociaux, Long nombreDePersonnesDansLeFoyer, Long parts, Long partsDeSoutien, boolean acceptationDesStatus) {
        this.id = id;
        this.genre = genre;
        this.prenom = prenom;
        this.nom = nom;
        this.telephone = telephone;
        this.email = email;
        this.adresse = adresse;
        this.ville = ville;
        this.codePostal = codePostal;
        this.etudiantOuMinimasSociaux = etudiantOuMinimasSociaux;
        this.nombreDePersonnesDansLeFoyer = nombreDePersonnesDansLeFoyer;
        this.parts = parts;
        this.partsDeSoutien = partsDeSoutien;
        this.acceptationDesStatus = acceptationDesStatus;
    }

    public static CooperateurDTO fromCooperateur(Cooperateur cooperateur) {
        return new CooperateurDTO(
                cooperateur.id,
                cooperateur.genre,
                cooperateur.prenom,
                cooperateur.nom,
                cooperateur.telephone,
                cooperateur.email,
                cooperateur.adresse,
                cooperateur.ville,
                cooperateur.codePostal,
                cooperateur.etudiantOuMinimasSociaux,
                cooperateur.nombreDePersonnesDansLeFoyer,
                cooperateur.parts,
                cooperateur.partsDeSoutien,
                cooperateur.acceptationDesStatus
        );
    }
}
