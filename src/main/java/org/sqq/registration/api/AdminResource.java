package org.sqq.registration.api;

import io.quarkus.logging.Log;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.sqq.registration.Cooperateur;
import org.sqq.registration.CooperateurStatus;
import org.sqq.registration.SouscriptionSupplementaire;
import org.sqq.registration.api.dto.CooperateurDTO;
import org.sqq.registration.api.dto.SouscriptionSupplementaireDTO;

import java.util.List;

@Path("/api/v1/administration")
@RolesAllowed("admin")
public class AdminResource {

    @Path("/cooperateurs")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<CooperateurDTO> list() {
        return Cooperateur.<Cooperateur>streamAll()
                .map(CooperateurDTO::fromCooperateur)
                .toList();
    }

    @Path("/cooperateurs/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public CooperateurDTO get(@PathParam("id") Long id) {
        Cooperateur cooperateur = Cooperateur.findById(id);
        return CooperateurDTO.fromCooperateur(cooperateur);
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
        if (cooperateur.status != CooperateurStatus.PAID) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Cooperateur must be PAID before processing")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        cooperateur.status = CooperateurStatus.PROCESSED;
        return Response.ok(CooperateurDTO.fromCooperateur(cooperateur)).build();
    }

    @Path("/parts-additionnelles")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SouscriptionSupplementaireDTO> listSouscriptionsSupplementaires() {
        return SouscriptionSupplementaire.<SouscriptionSupplementaire>streamAll()
                .map(SouscriptionSupplementaireDTO::fromSouscriptionSupplementaire)
                .toList();
    }

    @Path("/parts-additionnelles/{id}/process")
    @POST
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response markSouscriptionSupplementaireAsProcessed(@PathParam("id") Long id) {
        Log.infof("Processing souscription supplementaire %d", id);
        SouscriptionSupplementaire souscription = SouscriptionSupplementaire.findById(id);
        if (souscription == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (souscription.status != CooperateurStatus.PAID) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Souscription supplementaire must be PAID before processing")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        souscription.status = CooperateurStatus.PROCESSED;
        return Response.ok(SouscriptionSupplementaireDTO.fromSouscriptionSupplementaire(souscription)).build();
    }
}
