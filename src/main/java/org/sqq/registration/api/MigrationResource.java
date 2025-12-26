package org.sqq.registration.api;

import io.quarkus.logging.Log;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.sqq.registration.service.V1ToV2MigrationService;

@Path("/api/v2/migration")
@RolesAllowed("admin")
public class MigrationResource {

    @Inject
    V1ToV2MigrationService migrationService;

    @POST
    @Path("/from-v1")
    @Produces(MediaType.APPLICATION_JSON)
    public V1ToV2MigrationService.MigrationResult migrateFromV1() {
        Log.info("Starting V1 to V2 migration...");
        return migrationService.migrateFromV1();
    }
}
