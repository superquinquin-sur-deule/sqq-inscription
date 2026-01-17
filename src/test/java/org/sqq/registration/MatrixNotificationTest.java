package org.sqq.registration;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.stripe.exception.StripeException;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sqq.registration.stripe.Stripe;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import java.util.concurrent.TimeUnit;

@QuarkusTest
@TestProfile(MatrixNotificationTest.MatrixEnabledProfile.class)
class MatrixNotificationTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().port(9999))
            .build();

    public static class MatrixEnabledProfile implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    "matrix.enabled", "true",
                    "matrix.homeserver-url", "http://localhost:9999",
                    "matrix.access-token", "test-token",
                    "matrix.room-id", "!testroom:matrix.org",
                    "matrix.price-per-share", "30",
                    "quarkus.rest-client.matrix-api.url", "http://localhost:9999"
            );
        }
    }

    @BeforeAll
    public static void setup() throws StripeException, URISyntaxException {
        Stripe mockedStripe = Mockito.mock(Stripe.class);
        Mockito.when(mockedStripe.paySouscription(ArgumentMatchers.any())).thenReturn(new URI("https://checkout.stripe.com"));
        Mockito.when(mockedStripe.paySouscriptionSupplementaire(ArgumentMatchers.any())).thenReturn(new URI("https://checkout.stripe.com"));
        Mockito.when(mockedStripe.hasPaid(ArgumentMatchers.any())).thenReturn(true);
        Mockito.when(mockedStripe.hasPaidSouscriptionSupplementaire(ArgumentMatchers.any())).thenReturn(true);
        QuarkusMock.installMockForType(mockedStripe, Stripe.class);
    }

    @Test
    void shouldSendMatrixNotificationOnNewSubscriptionPayment() {
        // Stub Matrix API
        wireMock.stubFor(put(urlMatching("/_matrix/client/v3/rooms/.*/send/m.room.message/.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"event_id\": \"$test123\"}")));

        // Create a cooperateur
        given().redirects().follow(false)
                .formParam("genre", "MONSIEUR")
                .formParam("prenom", "TestPrenom")
                .formParam("nom", "TestNom")
                .formParam("telephone", "0600000099")
                .formParam("email", "matrix.test@mailbox.org")
                .formParam("adresse", "1 rue du Test")
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

        // Get the cooperateur ID
        Number id = given()
                .auth().preemptive().basic("admin", "password")
                .when().get("/api/v1/administration/cooperateurs")
                .then()
                .statusCode(200)
                .extract()
                .path("find { it.email == 'matrix.test@mailbox.org' }.id");

        // Trigger payment success
        given()
                .when().post("/api/v1/registrations/success/" + id)
                .then()
                .statusCode(200);

        // Wait for async notification and verify Matrix API was called
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                wireMock.verify(putRequestedFor(urlMatching("/_matrix/client/v3/rooms/!testroom:matrix.org/send/m.room.message/.*"))
                        .withHeader("Authorization", equalTo("Bearer test-token"))
                        .withRequestBody(containing("Nouveau paiement recu!"))
                        .withRequestBody(containing("TestPrenom"))
                        .withRequestBody(containing("TestNom"))
                        .withRequestBody(containing("matrix.test@mailbox.org"))));
    }

    @Test
    void shouldSendMatrixNotificationOnAdditionalSharesPayment() {
        // Stub Matrix API
        wireMock.stubFor(put(urlMatching("/_matrix/client/v3/rooms/.*/send/m.room.message/.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"event_id\": \"$test456\"}")));

        // Create additional shares subscription
        given().redirects().follow(false)
                .formParam("prenom", "AdditionalPrenom")
                .formParam("nom", "AdditionalNom")
                .formParam("email", "additional.test@mailbox.org")
                .formParam("partsSupplementaires", "5")
                .contentType("application/x-www-form-urlencoded")
                .when().post("/api/v1/parts-supplementaires")
                .then().statusCode(303);

        // Get the souscription ID
        Number id = given()
                .auth().preemptive().basic("admin", "password")
                .when().get("/api/v1/administration/parts-additionnelles")
                .then()
                .statusCode(200)
                .extract()
                .path("find { it.email == 'additional.test@mailbox.org' }.id");

        // Trigger payment success
        given()
                .when().post("/api/v1/parts-supplementaires/success/" + id)
                .then()
                .statusCode(200);

        // Wait for async notification and verify Matrix API was called
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                wireMock.verify(putRequestedFor(urlMatching("/_matrix/client/v3/rooms/!testroom:matrix.org/send/m.room.message/.*"))
                        .withHeader("Authorization", equalTo("Bearer test-token"))
                        .withRequestBody(containing("parts supplementaires"))
                        .withRequestBody(containing("AdditionalPrenom"))
                        .withRequestBody(containing("AdditionalNom"))
                        .withRequestBody(containing("additional.test@mailbox.org"))));
    }
}
