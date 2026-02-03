package org.sqq.registration.api;

import com.stripe.exception.StripeException;
import io.quarkus.logging.Log;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.sqq.registration.Cooperateur;
import org.sqq.registration.CooperateurStatus;
import org.sqq.registration.SouscriptionSupplementaire;
import org.sqq.registration.api.dto.RetryPaymentInfoDTO;
import org.sqq.registration.stripe.Stripe;

import java.net.URI;

@Path("/api/v1/retry-payment")
public class RetryPaymentResource {

    private final Stripe stripe;

    public RetryPaymentResource(Stripe stripe) {
        this.stripe = stripe;
    }

    @GET
    @Path("/cooperateur/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCooperateurPaymentInfo(@PathParam("uuid") String uuid) {
        Cooperateur cooperateur = Cooperateur.find("uuid", uuid).firstResult();
        if (cooperateur == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Souscription non trouvee")
                    .build();
        }
        if (cooperateur.status != CooperateurStatus.PAYMENT_PENDING) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Cette souscription a deja ete payee ou traitee")
                    .build();
        }
        return Response.ok(RetryPaymentInfoDTO.fromCooperateur(cooperateur)).build();
    }

    @POST
    @Path("/cooperateur/{uuid}")
    @Transactional
    public Response retryCooperateurPayment(@PathParam("uuid") String uuid) throws StripeException {
        Log.infof("Retry payment requested for cooperateur uuid=%s", uuid);

        Cooperateur cooperateur = Cooperateur.find("uuid", uuid).firstResult();
        if (cooperateur == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Souscription non trouvee")
                    .build();
        }
        if (cooperateur.status != CooperateurStatus.PAYMENT_PENDING) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Cette souscription a deja ete payee ou traitee")
                    .build();
        }

        URI paymentUrl = stripe.paySouscription(cooperateur);
        cooperateur.persist();

        return Response.seeOther(paymentUrl).build();
    }

    @GET
    @Path("/souscription/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSouscriptionPaymentInfo(@PathParam("uuid") String uuid) {
        SouscriptionSupplementaire souscription = SouscriptionSupplementaire.find("uuid", uuid).firstResult();
        if (souscription == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Souscription non trouvee")
                    .build();
        }
        if (souscription.status != CooperateurStatus.PAYMENT_PENDING) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Cette souscription a deja ete payee ou traitee")
                    .build();
        }
        return Response.ok(RetryPaymentInfoDTO.fromSouscriptionSupplementaire(souscription)).build();
    }

    @POST
    @Path("/souscription/{uuid}")
    @Transactional
    public Response retrySouscriptionPayment(@PathParam("uuid") String uuid) throws StripeException {
        Log.infof("Retry payment requested for souscription supplementaire uuid=%s", uuid);

        SouscriptionSupplementaire souscription = SouscriptionSupplementaire.find("uuid", uuid).firstResult();
        if (souscription == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Souscription non trouvee")
                    .build();
        }
        if (souscription.status != CooperateurStatus.PAYMENT_PENDING) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Cette souscription a deja ete payee ou traitee")
                    .build();
        }

        URI paymentUrl = stripe.paySouscriptionSupplementaire(souscription);
        souscription.persist();

        return Response.seeOther(paymentUrl).build();
    }
}
