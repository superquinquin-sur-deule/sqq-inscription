package org.sqq.registration.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.sqq.registration.Cooperateur;
import org.sqq.registration.api.dto.CooperateurDTO;

import java.util.List;

@Path("/api/v1/administration")
public class AdminResource {

    @Path("/cooperateurs")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<CooperateurDTO> list() {
        return Cooperateur.<Cooperateur>streamAll()
                .map(CooperateurDTO::fromCooperateur)
                .toList();
    }
}
