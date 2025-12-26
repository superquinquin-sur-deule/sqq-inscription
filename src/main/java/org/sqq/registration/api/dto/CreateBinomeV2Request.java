package org.sqq.registration.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.sqq.registration.Genre;

@Schema(description = "Request DTO for binome member data within a pair registration")
public class CreateBinomeV2Request {

    @NotNull
    @Schema(description = "Genre du binôme", example = "MONSIEUR")
    public Genre genre;

    @NotBlank
    @Schema(description = "Prénom du binôme", example = "Pierre")
    public String prenom;

    @NotBlank
    @Schema(description = "Nom du binôme", example = "Dupont")
    public String nom;

    @Schema(description = "Date de naissance du binôme", example = "1983-07-22")
    public String dateNaissance;

    @NotBlank
    @Schema(description = "Numéro de téléphone du binôme", example = "0698765432")
    public String telephone;

    @NotBlank
    @Schema(description = "Adresse email du binôme", example = "pierre.dupont@email.com")
    public String email;

    @Schema(description = "Adresse postale du binôme (si différente du principal)")
    public String adresse;

    @Schema(description = "Ville du binôme (si différente du principal)")
    public String ville;

    @Schema(description = "Code postal du binôme (si différent du principal)")
    public String codePostal;

    @Schema(description = "Notes administratives pour le binôme")
    public String notes;

    public CreateBinomeV2Request() {
    }
}
