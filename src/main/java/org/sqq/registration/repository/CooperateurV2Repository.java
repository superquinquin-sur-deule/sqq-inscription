package org.sqq.registration.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.sqq.registration.CooperateurV2;
import org.sqq.registration.MemberStatus;
import org.sqq.registration.PaymentStatus;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CooperateurV2Repository implements PanacheRepository<CooperateurV2> {

    public Optional<CooperateurV2> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public Optional<CooperateurV2> findByTelephone(String telephone) {
        return find("telephone", telephone).firstResultOptional();
    }

    public List<CooperateurV2> findByPaymentStatus(PaymentStatus status) {
        return list("paymentStatus", status);
    }

    public List<CooperateurV2> findByMemberStatus(MemberStatus status) {
        return list("memberStatus", status);
    }

    public List<CooperateurV2> search(String searchTerm) {
        String pattern = "%" + searchTerm.toLowerCase() + "%";
        return list("lower(nom) like ?1 or lower(prenom) like ?1 or lower(email) like ?1", pattern);
    }
}
