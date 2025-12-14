package org.sqq.registration.api.dto;

import org.sqq.registration.Binome;
import org.sqq.registration.Genre;

public class BinomeDTO {
    public Long id;
    public Genre genre;
    public String nom;
    public String prenom;
    public String adresse;
    public String ville;
    public String codePostal;
    public String email;
    public String telephone;
    public String dateNaissance;

    public static BinomeDTO fromBinome(Binome binome) {
        if (binome == null) {
            return null;
        }
        var dto = new BinomeDTO();
        dto.id = binome.id;
        dto.genre = binome.genre;
        dto.nom = binome.nom;
        dto.prenom = binome.prenom;
        dto.adresse = binome.adresse;
        dto.ville = binome.ville;
        dto.codePostal = binome.codePostal;
        dto.email = binome.email;
        dto.telephone = binome.telephone;
        dto.dateNaissance = binome.dateNaissance;
        return dto;
    }
}
