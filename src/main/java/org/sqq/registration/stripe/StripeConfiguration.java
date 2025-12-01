package org.sqq.registration.stripe;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "stripe")
public interface StripeConfiguration {
    String apiKey();
    String partSocialePriceId();
    String redirectDomain();
}
