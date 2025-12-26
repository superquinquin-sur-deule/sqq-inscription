package org.sqq.registration.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.sqq.registration.CooperateurRelationship;
import org.sqq.registration.CooperateurV2;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CooperateurRelationshipRepository implements PanacheRepository<CooperateurRelationship> {

    public List<CooperateurRelationship> findByCooperateur(CooperateurV2 cooperateur) {
        return list("cooperateur1 = ?1 or cooperateur2 = ?1", cooperateur);
    }

    public Optional<CooperateurV2> findPartner(CooperateurV2 cooperateur) {
        return find("cooperateur1 = ?1 or cooperateur2 = ?1", cooperateur)
                .firstResultOptional()
                .map(rel -> rel.cooperateur1.id.equals(cooperateur.id) ? rel.cooperateur2 : rel.cooperateur1);
    }

    public Optional<CooperateurRelationship> findRelationshipBetween(CooperateurV2 coop1, CooperateurV2 coop2) {
        return find("(cooperateur1 = ?1 and cooperateur2 = ?2) or (cooperateur1 = ?2 and cooperateur2 = ?1)", coop1, coop2)
                .firstResultOptional();
    }
}
