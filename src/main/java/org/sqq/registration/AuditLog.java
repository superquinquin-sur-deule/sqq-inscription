package org.sqq.registration;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
@Table(indexes = {
    @Index(columnList = "entityType"),
    @Index(columnList = "entityId"),
    @Index(columnList = "entityType, entityId")
})
public class AuditLog extends PanacheEntity {

    @NotBlank
    public String entityType;

    @NotNull
    public Long entityId;

    @NotBlank
    public String action;

    @Column(columnDefinition = "TEXT")
    public String changes;

    public String performedBy;

    @CreationTimestamp
    public Instant performedAt;

    public AuditLog() {
    }

    public static List<AuditLog> findByEntity(String entityType, Long entityId) {
        return list("entityType = ?1 and entityId = ?2", entityType, entityId);
    }

    public static List<AuditLog> findByEntityType(String entityType) {
        return list("entityType", entityType);
    }
}
