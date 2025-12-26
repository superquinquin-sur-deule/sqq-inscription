package org.sqq.registration;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
@Table(indexes = @Index(columnList = "cooperateur_id"))
public class StatusHistory extends PanacheEntity {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "cooperateur_id")
    public CooperateurV2 cooperateur;

    @NotBlank
    public String statusType;

    public String previousStatus;

    @NotBlank
    public String newStatus;

    @CreationTimestamp
    public Instant changedAt;

    public String changedBy;

    public StatusHistory() {
    }

    public static List<StatusHistory> findByCooperateur(CooperateurV2 cooperateur) {
        return list("cooperateur", cooperateur);
    }

    public static List<StatusHistory> findByCooperateurAndType(CooperateurV2 cooperateur, String statusType) {
        return list("cooperateur = ?1 and statusType = ?2", cooperateur, statusType);
    }
}
