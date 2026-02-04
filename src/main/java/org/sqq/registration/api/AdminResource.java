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
import java.util.stream.Stream;

@Path("/api/v1/administration")
@RolesAllowed("admin")
public class AdminResource {

    @Path("/cooperateurs")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<CooperateurDTO> list(@QueryParam("statuses") List<CooperateurStatus> statuses) {
        Stream<Cooperateur> stream;
        if (statuses != null && !statuses.isEmpty()) {
            stream = Cooperateur.<Cooperateur>stream("status in ?1", statuses);
        } else {
            stream = Cooperateur.<Cooperateur>stream("status != ?1", CooperateurStatus.ARCHIVED);
        }
        return stream.map(CooperateurDTO::fromCooperateur).toList();
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
    public List<SouscriptionSupplementaireDTO> listSouscriptionsSupplementaires(@QueryParam("statuses") List<CooperateurStatus> statuses) {
        Stream<SouscriptionSupplementaire> stream;
        if (statuses != null && !statuses.isEmpty()) {
            stream = SouscriptionSupplementaire.<SouscriptionSupplementaire>stream("status in ?1", statuses);
        } else {
            stream = SouscriptionSupplementaire.<SouscriptionSupplementaire>stream("status != ?1", CooperateurStatus.ARCHIVED);
        }
        return stream.map(SouscriptionSupplementaireDTO::fromSouscriptionSupplementaire).toList();
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

    @Path("/cooperateurs/{id}/archive")
    @POST
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response archiveCooperateur(@PathParam("id") Long id) {
        Log.infof("Archiving cooperateur %d", id);
        Cooperateur cooperateur = Cooperateur.findById(id);
        if (cooperateur == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (cooperateur.status != CooperateurStatus.PAYMENT_PENDING) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Seules les souscriptions en attente de paiement peuvent être archivées")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        cooperateur.status = CooperateurStatus.ARCHIVED;
        return Response.ok(CooperateurDTO.fromCooperateur(cooperateur)).build();
    }

    @Path("/parts-additionnelles/{id}/archive")
    @POST
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response archiveSouscriptionSupplementaire(@PathParam("id") Long id) {
        Log.infof("Archiving souscription supplementaire %d", id);
        SouscriptionSupplementaire souscription = SouscriptionSupplementaire.findById(id);
        if (souscription == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (souscription.status != CooperateurStatus.PAYMENT_PENDING) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Seules les souscriptions en attente de paiement peuvent être archivées")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        souscription.status = CooperateurStatus.ARCHIVED;
        return Response.ok(SouscriptionSupplementaireDTO.fromSouscriptionSupplementaire(souscription)).build();
    }
}
