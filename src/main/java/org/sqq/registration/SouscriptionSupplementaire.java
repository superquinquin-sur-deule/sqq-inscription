package org.sqq.registration;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
public class SouscriptionSupplementaire extends PanacheEntity {
    @NotBlank
    public String prenom;
    @NotBlank
    public String nom;
    @NotBlank
    @Email
    public String email;
    @NotNull
    @Min(1)
    public Long partsSupplementaires;
    @NotNull
    public CooperateurStatus status;
    public String stripeSessionId;
    @CreationTimestamp
    public Instant createdAt;
    @UpdateTimestamp
    public Instant updatedAt;

    public SouscriptionSupplementaire() {
    }

    public SouscriptionSupplementaire(String prenom, String nom, String email, Long partsSupplementaires) {
        this.prenom = prenom;
        this.nom = nom;
        this.email = email;
        this.partsSupplementaires = partsSupplementaires;
        this.status = CooperateurStatus.PAYMENT_PENDING;
    }
}
