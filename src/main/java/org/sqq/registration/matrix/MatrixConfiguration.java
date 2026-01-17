package org.sqq.registration.matrix;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

import java.util.Optional;

@ConfigMapping(prefix = "matrix")
public interface MatrixConfiguration {
    Optional<String> homeserverUrl();
    Optional<String> accessToken();
    Optional<String> roomId();

    @WithDefault("30")
    int pricePerShare();

    @WithDefault("false")
    boolean enabled();
}
