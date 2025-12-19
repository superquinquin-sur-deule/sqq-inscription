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

    public Boolean hasPaid(Cooperateur cooperateur) {
        try {
            Session session = Session.retrieve(cooperateur.stripeSessionId);
            boolean paid = session.getPaymentStatus().equals("paid");

            Log.infof("Stripe session payment status is %s for cooperateur %d", paid ? "paid" : "not paid", cooperateur.id);

            return paid;
        } catch (StripeException e) {
            Log.error("Unable to retrieve Stripe session", e);
            return false;
        }
    }

    public URI paySouscriptionSupplementaire(org.sqq.registration.SouscriptionSupplementaire souscription) throws StripeException {
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(stripeConfiguration.redirectDomain() + "/parts-supplementaires-success?souscriptionId=" + souscription.id)
                        .setCustomerEmail(souscription.email)
                        .setLocale(SessionCreateParams.Locale.FR)
                        .setCurrency("eur")
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(souscription.partsSupplementaires)
                                        .setPrice(stripeConfiguration.partSocialePriceId())
                                        .build())
                        .build();

        Session session = Session.create(params);

        souscription.stripeSessionId = session.getId();

        return URI.create(session.getUrl());
    }

    public Boolean hasPaidSouscriptionSupplementaire(org.sqq.registration.SouscriptionSupplementaire souscription) {
        try {
            Session session = Session.retrieve(souscription.stripeSessionId);
            boolean paid = session.getPaymentStatus().equals("paid");

            Log.infof("Stripe session payment status is %s for souscription supplementaire %d", paid ? "paid" : "not paid", souscription.id);

            return paid;
        } catch (StripeException e) {
            Log.error("Unable to retrieve Stripe session", e);
            return false;
        }
    }
}
