package org.sqq.registration.api;

import com.stripe.exception.StripeException;
import io.quarkus.logging.Log;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.sqq.registration.CooperateurStatus;
import org.sqq.registration.SouscriptionSupplementaire;
import org.sqq.registration.stripe.Stripe;

import java.net.URI;

@Path("/api/v1/parts-supplementaires")
public class AdditionalSharesResource {
    private final Stripe stripe;
    private final Validator validator;

    public AdditionalSharesResource(Stripe stripe, Validator validator) {
        this.stripe = stripe;
        this.validator = validator;
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public Response registerForm(
            @FormParam("prenom") String prenom,
            @FormParam("nom") String nom,
            @FormParam("email") String email,
            @FormParam("partsSupplementaires") String partsSupplementaires
    ) throws StripeException {
        Log.infof("New additional shares subscription: %s %s %s", prenom, nom, email);

        SouscriptionSupplementaire souscription = new SouscriptionSupplementaire(
                prenom,
                nom,
                email,
                Long.parseLong(partsSupplementaires)
        );

        validator.validate(souscription);
        souscription.persist();

        URI paymentUrl = stripe.paySouscriptionSupplementaire(souscription);
        return Response.seeOther(paymentUrl).build();
    }

    @POST
    @Path("/success/{souscriptionId}")
    @Transactional
    public Response successfulPayment(@PathParam("souscriptionId") Long souscriptionId) {
        Log.infof("Payment successful for souscription supplementaire %d", souscriptionId);
        SouscriptionSupplementaire souscription = SouscriptionSupplementaire.findById(souscriptionId);
        if (souscription == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (stripe.hasPaidSouscriptionSupplementaire(souscription)) {
            souscription.status = CooperateurStatus.PAID;
            souscription.persist();
            return Response.ok().build();
        } else {
            Log.errorf("Stripe session payment status is not paid for souscription supplementaire %d", souscriptionId);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}
