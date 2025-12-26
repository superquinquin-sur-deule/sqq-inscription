package org.sqq.registration.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.sqq.registration.CooperateurV2;
import org.sqq.registration.StatusHistory;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class StatusHistoryRepository implements PanacheRepository<StatusHistory> {

    public List<StatusHistory> findByCooperateur(CooperateurV2 cooperateur) {
        return list("cooperateur order by changedAt desc", cooperateur);
    }

    public List<StatusHistory> findByCooperateurId(Long cooperateurId) {
        return list("cooperateur.id = ?1 order by changedAt desc", cooperateurId);
    }

    public List<StatusHistory> findByCooperateurAndType(CooperateurV2 cooperateur, String statusType) {
        return list("cooperateur = ?1 and statusType = ?2 order by changedAt desc", cooperateur, statusType);
    }

    public Optional<StatusHistory> findLatestByCooperateur(CooperateurV2 cooperateur) {
        return find("cooperateur = ?1 order by changedAt desc", cooperateur).firstResultOptional();
    }

    public Optional<StatusHistory> findLatestByCooperateurAndType(CooperateurV2 cooperateur, String statusType) {
        return find("cooperateur = ?1 and statusType = ?2 order by changedAt desc", cooperateur, statusType)
                .firstResultOptional();
    }
}
