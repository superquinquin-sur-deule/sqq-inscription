package org.sqq.registration.api;

import io.quarkus.logging.Log;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.sqq.registration.Cooperateur;
import org.sqq.registration.CooperateurStatus;
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

    @Path("/cooperateurs/{id}/process")
    @POST
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response markAsProcessed(@PathParam("id") Long id) {
        Log.infof("Processing cooperateur %d", id);
        Cooperateur cooperateur = Cooperateur.findById(id);
        if (cooperateur == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        cooperateur.status = CooperateurStatus.PROCESSED;
        return Response.ok(CooperateurDTO.fromCooperateur(cooperateur)).build();
    }
}
