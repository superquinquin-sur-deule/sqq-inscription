package org.sqq.registration.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.sqq.registration.Genre;
import org.sqq.registration.MemberStatus;
import org.sqq.registration.PaymentStatus;

@Schema(description = "Request DTO for creating a solo cooperateur")
public class CreateCooperateurV2Request {

    @NotNull
    @Schema(description = "Genre du coopérateur", example = "MADAME")
    public Genre genre;

    @NotBlank
    @Schema(description = "Prénom du coopérateur", example = "Marie")
    public String prenom;

    @NotBlank
    @Schema(description = "Nom du coopérateur", example = "Dupont")
    public String nom;

    @Schema(description = "Date de naissance", example = "1985-03-15")
    public String dateNaissance;

    @NotBlank
    @Schema(description = "Numéro de téléphone", example = "0612345678")
    public String telephone;

    @NotBlank
    @Schema(description = "Adresse email", example = "marie.dupont@email.com")
    public String email;

    @NotBlank
    @Schema(description = "Adresse postale", example = "123 rue de la Paix")
    public String adresse;

    @NotBlank
    @Schema(description = "Ville", example = "Paris")
    public String ville;

    @NotBlank
    @Schema(description = "Code postal", example = "75001")
    public String codePostal;

    @Schema(description = "Étudiant ou bénéficiaire des minimas sociaux (tarif réduit)", example = "false")
    public Boolean etudiantOuMinimasSociaux;

    @Min(1)
    @Schema(description = "Nombre de personnes dans le foyer", example = "2")
    public Long nombreDePersonnesDansLeFoyer;

    @Schema(description = "Parts de soutien supplémentaires", example = "0")
    public Long partsDeSoutien;

    @Schema(description = "Statut de paiement initial", example = "PAID")
    public PaymentStatus paymentStatus;

    @Schema(description = "Statut de membre initial", example = "ACTIVE")
    public MemberStatus memberStatus;

    @Schema(description = "Notes administratives", example = "Inscription manuelle suite à demande par courrier")
    public String notes;

    public CreateCooperateurV2Request() {
    }
}
