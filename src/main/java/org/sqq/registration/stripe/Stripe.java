package org.sqq.registration.stripe;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.sqq.registration.Cooperateur;

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
                        .setSuccessUrl(stripeConfiguration.redirectDomain() + "/success.html")
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

        return URI.create(session.getUrl());
    }
}
