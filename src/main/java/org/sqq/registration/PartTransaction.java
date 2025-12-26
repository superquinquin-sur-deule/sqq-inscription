package org.sqq.registration;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
@Table(indexes = @Index(columnList = "cooperateur_id"))
public class PartTransaction extends PanacheEntity {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "cooperateur_id")
    public CooperateurV2 cooperateur;

    @NotNull
    @Enumerated(EnumType.STRING)
    public PartTransactionType transactionType;

    @NotNull
    public Long parts;

    public String reason;

    public String stripeSessionId;

    @CreationTimestamp
    public Instant createdAt;

    public PartTransaction() {
    }

    public static Long computeTotalParts(CooperateurV2 cooperateur) {
        return computeTotalPartsById(cooperateur.id);
    }

    public static Long computeTotalPartsById(Long cooperateurId) {
        return getEntityManager()
                .createQuery("SELECT COALESCE(SUM(p.parts), 0) FROM PartTransaction p WHERE p.cooperateur.id = :id", Long.class)
                .setParameter("id", cooperateurId)
                .getSingleResult();
    }

    public static List<PartTransaction> findByCooperateur(CooperateurV2 cooperateur) {
        return list("cooperateur", cooperateur);
    }
}
