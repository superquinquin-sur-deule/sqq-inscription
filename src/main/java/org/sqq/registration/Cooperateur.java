package org.sqq.registration;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Cooperateur extends PanacheEntity {
    @NotNull
    public Genre genre;
    @NotBlank
    public String prenom;
    @NotBlank
    public String nom;
    @NotBlank
    public String telephone;
    @NotBlank
    public String email;
    @NotBlank
    public String adresse;
    @NotBlank
    public String ville;
    @NotBlank
    public String codePostal;
    public Boolean etudiantOuMinimasSociaux;
    @Min(1)
    public Long nombreDePersonnesDansLeFoyer;
    @Min(1)
    public Long parts;
    public Long partsDeSoutien;
    @OneToOne
    public Binome binome;
    @NotNull
    public boolean acceptationDesStatus;
    public CooperateurStatus status;
    public String stripeSessionId;

    public Cooperateur() {
    }

    public Cooperateur(Genre genre, String prenom, String nom, String telephone, String email, String adresse, String ville, String codePostal, Boolean etudiantOuMinimasSociaux, Long nombreDePersonnesDansLeFoyer, Long partsDeSoutien, Binome binome, boolean acceptationDesStatus) {
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
        this.partsDeSoutien = partsDeSoutien;
        this.acceptationDesStatus = acceptationDesStatus;
        this.status = CooperateurStatus.PAYMENT_PENDING;

        this.computeParts(etudiantOuMinimasSociaux, partsDeSoutien, binome);
    }

    public void computeParts(Boolean etudiantOuMinimasSociaux, Long partsDeSoutien, Binome binome) {
        long totalParts = 0L;

        if (etudiantOuMinimasSociaux) {
            totalParts += 1;
        } else {
            totalParts += 10;
        }

        if (binome != null) {
            totalParts += 2;
        }

        if (partsDeSoutien != null) {
            totalParts += partsDeSoutien;
        }

        this.parts = totalParts;
    }
}
