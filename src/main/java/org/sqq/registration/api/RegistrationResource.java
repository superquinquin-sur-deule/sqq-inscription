package org.sqq.registration.api;

import com.stripe.exception.StripeException;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sqq.registration.Cooperateur;
import org.sqq.registration.api.dto.CreateCooperateurDTO;
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
    @RequestBody(content = @org.eclipse.microprofile.openapi.annotations.media.Content(
            mediaType = MediaType.APPLICATION_JSON,
            examples = @ExampleObject(
                    name = "default",
                    value = """
                            {
                              "genre": "MADAME",
                              "prenom": "Jean-Michel",
                              "nom": "Dubois",
                              "telephone": "0772664457",
                              "email": "jean.michel@dubois.com",
                              "adresse": "7 rue des Bleuets",
                              "ville": "Lomme",
                              "codePostal": "59160",
                              "etudiantOuMinimasSociaux": false,
                              "nombreDePersonnesDansLeFoyer": 2,
                              "partsDeSoutien": 0,
                              "acceptationDesStatus": true
                            }
                            """
            )
    ))
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response register(CreateCooperateurDTO dto) throws StripeException {
        Cooperateur cooperateur = dto.toCooperateur();

        validator.validate(cooperateur);
        cooperateur.persist();

//        URI paymentUrl = stripe.paySouscription(cooperateur);
        
//        return Response.temporaryRedirect(paymentUrl).build();
        return Response.temporaryRedirect(URI.create("https://google.com?test=1234")).build();
    }

}
