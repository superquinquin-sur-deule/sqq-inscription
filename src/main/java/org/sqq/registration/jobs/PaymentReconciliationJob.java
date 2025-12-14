package org.sqq.registration.jobs;

import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.sqq.registration.Cooperateur;
import org.sqq.registration.CooperateurStatus;
import org.sqq.registration.stripe.Stripe;

import java.util.List;

@ApplicationScoped
public class PaymentReconciliationJob {

    @Inject
    Stripe stripe;

    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    void reconcilePendingPayments() {
        List<Cooperateur> pending = Cooperateur.find("status = ?1 and stripeSessionId is not null", CooperateurStatus.PAYMENT_PENDING)
                .list();

        if (pending.isEmpty()) {
            Log.info("PaymentReconciliationJob: no pending cooperateurs to check.");
            return;
        }

        Log.infof("PaymentReconciliationJob: checking %d pending cooperateurs with Stripe", pending.size());

        int updated = 0;
        for (Cooperateur c : pending) {
            try {
                if (stripe.hasPaid(c)) {
                    c.status = CooperateurStatus.PAID;
                    c.persist();
                    updated++;
                    Log.infof("PaymentReconciliationJob: cooperateur %d marked as PAID", c.id);
                }
            } catch (Exception e) {
                Log.errorf(e, "PaymentReconciliationJob: error while checking cooperateur %d", c.id);
            }
        }

        Log.infof("PaymentReconciliationJob: reconciliation completed. %d/%d updated to PAID", updated, pending.size());
    }
}
