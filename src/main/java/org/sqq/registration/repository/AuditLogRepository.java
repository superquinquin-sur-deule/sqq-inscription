package org.sqq.registration.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.sqq.registration.AuditLog;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class AuditLogRepository implements PanacheRepository<AuditLog> {

    public List<AuditLog> findByEntity(String entityType, Long entityId) {
        return list("entityType = ?1 and entityId = ?2 order by performedAt desc", entityType, entityId);
    }

    public List<AuditLog> findByEntityType(String entityType) {
        return list("entityType = ?1 order by performedAt desc", entityType);
    }

    public List<AuditLog> findByPerformedBy(String performedBy) {
        return list("performedBy = ?1 order by performedAt desc", performedBy);
    }

    public List<AuditLog> findByDateRange(Instant start, Instant end) {
        return list("performedAt >= ?1 and performedAt <= ?2 order by performedAt desc", start, end);
    }

    public List<AuditLog> findByEntityAndDateRange(String entityType, Long entityId, Instant start, Instant end) {
        return list("entityType = ?1 and entityId = ?2 and performedAt >= ?3 and performedAt <= ?4 order by performedAt desc",
                entityType, entityId, start, end);
    }
}
