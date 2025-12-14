package org.sqq.registration.api;

import com.stripe.exception.StripeException;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sqq.registration.stripe.Stripe;

import java.net.URI;
import java.net.URISyntaxException;

import static io.restassured.RestAssured.given;

@QuarkusTest
class RegistrationResourceTest {
    
    @Inject
    Stripe stripe;

    @BeforeAll
    public static void setup() throws StripeException, URISyntaxException {
        Stripe mockedStripe = Mockito.mock(Stripe.class);
        Mockito.when(mockedStripe.paySouscription(ArgumentMatchers.any())).thenReturn(new URI("https://checkout.stripe.com"));
        QuarkusMock.installMockForType(mockedStripe, Stripe.class);
    }
    
    @Test
    void shouldCreateCooperateur() {
        given()
                .formParam("genre", "MADAME")
                .formParam("prenom", "Jean")
                .formParam("nom", "Michel")
                .formParam("telephone", "0600000000")
                .formParam("email", "jean.michel@mailbox.org")
                .formParam("adresse", "184 rue du marais")
                .formParam("ville", "Lille")
                .formParam("codePostal", "59000")
                .formParam("etudiantOuMinimasSociaux", "false")
                .formParam("nombreDePersonnesDansLeFoyer", "2")
                .formParam("partsDeSoutien", "0")
                .formParam("acceptationDesStatus", "true")
                .formParam("binomeEnabled", "false")
                .contentType("application/x-www-form-urlencoded")
                .when().post("/api/v1/registrations")
                .then().statusCode(303);
    }
}