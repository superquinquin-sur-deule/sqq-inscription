package org.sqq.registration.stripe;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.sqq.registration.Cooperateur;
import org.sqq.registration.CooperateurStatus;

import java.net.URI;

@ApplicationScoped
public class Stripe {
    private final StripeConfiguration stripeConfiguration;

    public Stripe(StripeConfiguration stripeConfiguration) {
        this.stripeConfiguration = stripeConfiguration;
    }
    
    public void onStart(@Observes StartupEvent startupEvent) {
        com.stripe.Stripe.apiKey = stripeConfiguration.apiKey();
    }

    public URI paySouscription(Cooperateur cooperateur) throws StripeException {
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(stripeConfiguration.redirectDomain() + "/success?cooperateurId=" + cooperateur.id)
                        .setCustomerEmail(cooperateur.email)
                        .setLocale(SessionCreateParams.Locale.FR)
                        .setCurrency("eur")
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(cooperateur.parts)
                                        .setPrice(stripeConfiguration.partSocialePriceId())
                                        .build())
                        .build();
        
        Session session = Session.create(params);
        
        cooperateur.stripeSessionId = session.getId();

        return URI.create(session.getUrl());
    }
    
    public void updatePaymentStatus(Cooperateur cooperateur) throws StripeException {
        Session session = Session.retrieve(cooperateur.stripeSessionId);
        if (session.getPaymentStatus().equals("paid")) {
            Log.infof("Stripe session payment status is paid for cooperateur %d", cooperateur.id);
            cooperateur.status = CooperateurStatus.PAID;
        } else {
            Log.warnf("Stripe session payment status is not paid: %s", session.getPaymentStatus());
        }
        
        cooperateur.persist();
    }
}
