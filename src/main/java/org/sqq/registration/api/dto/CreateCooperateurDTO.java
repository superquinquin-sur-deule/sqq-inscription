package org.sqq.registration.api.dto;

import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.sqq.registration.Cooperateur;
import org.sqq.registration.Genre;

public class CreateCooperateurDTO {
    @Parameter(description = "Genre du cooperateur", example = "MADAME")
    public Genre genre;
    @Parameter(description = "Prénom du cooperateur", example = "Jean Michel")
    public String prenom;
    public String nom;
    public String telephone;
    public String email;
    public String adresse;
    public String ville;
    public String codePostal;
    public Boolean etudiantOuMinimasSociaux;
    public Long nombreDePersonnesDansLeFoyer;
    public Long partsDeSoutien;
    public boolean acceptationDesStatus;

    public Cooperateur toCooperateur() {
        return new Cooperateur(genre, prenom, nom, telephone, email, adresse, ville, codePostal, etudiantOuMinimasSociaux, nombreDePersonnesDansLeFoyer, partsDeSoutien, null, acceptationDesStatus);
    }
}
