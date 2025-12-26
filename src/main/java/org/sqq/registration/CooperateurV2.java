package org.sqq.registration;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;

import java.util.List;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
public class CooperateurV2 extends PanacheEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    public Genre genre;

    @NotBlank
    public String prenom;

    @NotBlank
    public String nom;

    public String dateNaissance;

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

    @NotNull
    public Boolean acceptationDesStatus;

    @Enumerated(EnumType.STRING)
    public PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    public MemberStatus memberStatus;

    @Enumerated(EnumType.STRING)
    public DataSource source;

    public String notes;

    public String stripeSessionId;

    @CreationTimestamp
    public Instant createdAt;

    @UpdateTimestamp
    public Instant updatedAt;

    @OneToMany(mappedBy = "cooperateur1")
    public List<CooperateurRelationship> relationshipsAsFirst;

    @OneToMany(mappedBy = "cooperateur2")
    public List<CooperateurRelationship> relationshipsAsSecond;

    public CooperateurV2() {
    }
}
