package org.sqq.registration.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.sqq.registration.CooperateurV2;
import org.sqq.registration.PartTransaction;
import org.sqq.registration.PartTransactionType;

import java.util.List;

@ApplicationScoped
public class PartTransactionRepository implements PanacheRepository<PartTransaction> {

    public List<PartTransaction> findByCooperateur(CooperateurV2 cooperateur) {
        return list("cooperateur", cooperateur);
    }

    public List<PartTransaction> findByCooperateurId(Long cooperateurId) {
        return list("cooperateur.id", cooperateurId);
    }

    public List<PartTransaction> findByTransactionType(PartTransactionType type) {
        return list("transactionType", type);
    }

    public List<PartTransaction> findByCooperateurAndType(CooperateurV2 cooperateur, PartTransactionType type) {
        return list("cooperateur = ?1 and transactionType = ?2", cooperateur, type);
    }

    public Long computeTotalParts(CooperateurV2 cooperateur) {
        return computeTotalPartsById(cooperateur.id);
    }

    public Long computeTotalPartsById(Long cooperateurId) {
        Long result = getEntityManager()
                .createQuery("SELECT COALESCE(SUM(p.parts), 0) FROM PartTransaction p WHERE p.cooperateur.id = :id", Long.class)
                .setParameter("id", cooperateurId)
                .getSingleResult();
        return result;
    }
}
