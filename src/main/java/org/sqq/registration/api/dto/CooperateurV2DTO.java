package org.sqq.registration.api.dto;

import org.sqq.registration.DataSource;
import org.sqq.registration.Genre;
import org.sqq.registration.MemberStatus;
import org.sqq.registration.PaymentStatus;

import java.time.Instant;

public class CooperateurV2DTO {

    // Entity fields (direct mapping)
    public Long id;
    public Genre genre;
    public String prenom;
    public String nom;
    public String dateNaissance;
    public String telephone;
    public String email;
    public String adresse;
    public String ville;
    public String codePostal;
    public Boolean etudiantOuMinimasSociaux;
    public Long nombreDePersonnesDansLeFoyer;
    public PaymentStatus paymentStatus;
    public MemberStatus memberStatus;
    public DataSource source;
    public String notes;
    public Instant createdAt;
    public Instant updatedAt;

    // Computed fields
    public Long totalParts;
    public String memberType;
    public Long partnerId;
    public String partnerName;

    public CooperateurV2DTO() {
    }
}
