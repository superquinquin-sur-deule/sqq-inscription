package org.sqq.registration.api;

import com.stripe.exception.StripeException;
import io.quarkus.logging.Log;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.sqq.registration.Binome;
import org.sqq.registration.Cooperateur;
import org.sqq.registration.CooperateurStatus;
import org.sqq.registration.Genre;
import org.sqq.registration.stripe.Stripe;

import java.net.URI;

@Path("/api/v1/registrations")
public class RegistrationResource {
    private final Stripe stripe;
    private final Validator validator;

    public RegistrationResource(Stripe stripe, Validator validator) {
        this.stripe = stripe;
        this.validator = validator;
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public Response registerForm(
            @FormParam("genre") String genre,
            @FormParam("prenom") String prenom,
            @FormParam("nom") String nom,
            @FormParam("telephone") String telephone,
            @FormParam("email") String email,
            @FormParam("adresse") String adresse,
            @FormParam("ville") String ville,
            @FormParam("codePostal") String codePostal,
            @FormParam("etudiantOuMinimasSociaux") String etuOuMinimas,
            @FormParam("nombreDePersonnesDansLeFoyer") String nbFoyer,
            @FormParam("partsDeSoutien") String partsDeSoutien,
            @FormParam("acceptationDesStatus") String acceptation,
            @FormParam("binomeEnabled") String binomeEnabled,
            @FormParam("binomeGenre") String binomeGenre,
            @FormParam("binomeNom") String binomeNom,
            @FormParam("binomePrenom") String binomePrenom,
            @FormParam("binomeAdresse") String binomeAdresse,
            @FormParam("binomeVille") String binomeVille,
            @FormParam("binomeCodePostal") String binomeCodePostal,
            @FormParam("binomeEmail") String binomeEmail,
            @FormParam("binomeTelephone") String binomeTelephone,
            @FormParam("binomeDateNaissance") String binomeDateNaissance
    ) throws StripeException {
        Log.infof("New registration: %s %s %s", prenom, nom, email);
        
        Binome binome = null;
        if (Boolean.parseBoolean(binomeEnabled)) {
            binome = new Binome();
            binome.genre = binomeGenre != null && !binomeGenre.isBlank() ? Genre.valueOf(binomeGenre) : null;
            binome.nom = binomeNom;
            binome.prenom = binomePrenom;
            binome.adresse = binomeAdresse;
            binome.ville = binomeVille;
            binome.codePostal = binomeCodePostal;
            binome.email = binomeEmail;
            binome.telephone = binomeTelephone;
            binome.dateNaissance = binomeDateNaissance;
            binome.persist();
        }
        
        Cooperateur cooperateur = new Cooperateur(
                Genre.valueOf(genre),
                prenom,
                nom,
                telephone,
                email,
                adresse,
                ville,
                codePostal,
                Boolean.parseBoolean(etuOuMinimas),
                Long.parseLong(nbFoyer),
                Long.parseLong(partsDeSoutien),
                binome,
                Boolean.parseBoolean(acceptation)
        );

        validator.validate(cooperateur);
        cooperateur.persist();

        URI paymentUrl = stripe.paySouscription(cooperateur);
        return Response.seeOther(paymentUrl).build();
    }
    
    @POST
    @Path("/success/{cooperateurId}")
    @Transactional
    public Response successfulPayment(@PathParam("cooperateurId") Long cooperateurId) {
        Log.infof("Payment successful for cooperateur %d", cooperateurId);
        Cooperateur cooperateur = Cooperateur.findById(cooperateurId);
        if (stripe.hasPaid(cooperateur)) {
            cooperateur.status = CooperateurStatus.PAID;
            cooperateur.persist();
            return Response.ok().build();
        } else {
            Log.errorf("Stripe session payment status is not paid for cooperateur %d", cooperateurId);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}
