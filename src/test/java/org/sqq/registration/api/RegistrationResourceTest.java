package org.sqq.registration.api;

import com.stripe.exception.StripeException;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sqq.registration.stripe.Stripe;

import java.net.URI;
import java.net.URISyntaxException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class RegistrationResourceTest {

    @BeforeAll
    public static void setup() throws StripeException, URISyntaxException {
        Stripe mockedStripe = Mockito.mock(Stripe.class);
        Mockito.when(mockedStripe.paySouscription(ArgumentMatchers.any())).thenReturn(new URI("https://checkout.stripe.com"));
        QuarkusMock.installMockForType(mockedStripe, Stripe.class);
    }
    
    @Test
    void shouldCreateCooperateur() {
        given().redirects().follow(false)
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

        given()
                .auth().preemptive().basic("admin", "admin")
                .when().get("/api/v1/administration/cooperateurs")
                .then()
                .statusCode(200)
                .body("find { it.email == 'jean.michel@mailbox.org' }.nom", equalTo("Michel"))
                .body("find { it.email == 'jean.michel@mailbox.org' }.prenom", equalTo("Jean"))
                .body("find { it.email == 'jean.michel@mailbox.org' }.binome", nullValue());
    }

    @Test
    void shouldCreateCooperateurWithBinome() {
        given().redirects().follow(false)
                .formParam("genre", "MONSIEUR")
                .formParam("prenom", "Pierre")
                .formParam("nom", "Dupont")
                .formParam("telephone", "0612345678")
                .formParam("email", "pierre.dupont@mailbox.org")
                .formParam("adresse", "42 avenue des Fleurs")
                .formParam("ville", "Lille")
                .formParam("codePostal", "59800")
                .formParam("etudiantOuMinimasSociaux", "false")
                .formParam("nombreDePersonnesDansLeFoyer", "3")
                .formParam("partsDeSoutien", "2")
                .formParam("acceptationDesStatus", "true")
                .formParam("binomeEnabled", "true")
                .formParam("binomeGenre", "MADAME")
                .formParam("binomeNom", "Dupont")
                .formParam("binomePrenom", "Marie")
                .formParam("binomeAdresse", "42 avenue des Fleurs")
                .formParam("binomeVille", "Lille")
                .formParam("binomeCodePostal", "59800")
                .formParam("binomeEmail", "marie.dupont@mailbox.org")
                .formParam("binomeTelephone", "0698765432")
                .formParam("binomeDateNaissance", "1985-06-15")
                .contentType("application/x-www-form-urlencoded")
                .when().post("/api/v1/registrations")
                .then().statusCode(303);

        given()
                .auth().preemptive().basic("admin", "admin")
                .when().get("/api/v1/administration/cooperateurs")
                .then()
                .statusCode(200)
                .body("find { it.email == 'pierre.dupont@mailbox.org' }.nom", equalTo("Dupont"))
                .body("find { it.email == 'pierre.dupont@mailbox.org' }.prenom", equalTo("Pierre"))
                .body("find { it.email == 'pierre.dupont@mailbox.org' }.binome", notNullValue())
                .body("find { it.email == 'pierre.dupont@mailbox.org' }.binome.nom", equalTo("Dupont"))
                .body("find { it.email == 'pierre.dupont@mailbox.org' }.binome.prenom", equalTo("Marie"))
                .body("find { it.email == 'pierre.dupont@mailbox.org' }.binome.genre", equalTo("MADAME"));
    }

    @Test
    void shouldProcessCooperateur() {
        given().redirects().follow(false)
                .formParam("genre", "MADAME")
                .formParam("prenom", "Lucie")
                .formParam("nom", "Bernard")
                .formParam("telephone", "0600000001")
                .formParam("email", "lucie.bernard@mailbox.org")
                .formParam("adresse", "12 rue des Lilas")
                .formParam("ville", "Lille")
                .formParam("codePostal", "59000")
                .formParam("etudiantOuMinimasSociaux", "false")
                .formParam("nombreDePersonnesDansLeFoyer", "1")
                .formParam("partsDeSoutien", "0")
                .formParam("acceptationDesStatus", "true")
                .formParam("binomeEnabled", "false")
                .contentType("application/x-www-form-urlencoded")
                .when().post("/api/v1/registrations")
                .then().statusCode(303);

        Number id = given()
                .auth().preemptive().basic("admin", "admin")
                .when().get("/api/v1/administration/cooperateurs")
                .then()
                .statusCode(200)
                .extract()
                .path("find { it.email == 'lucie.bernard@mailbox.org' }.id");

        given()
                .auth().preemptive().basic("admin", "admin")
                .when().post("/api/v1/administration/cooperateurs/" + id + "/process")
                .then()
                .statusCode(200)
                .body("status", equalTo("PROCESSED"));

        given()
                .auth().preemptive().basic("admin", "admin")
                .when().get("/api/v1/administration/cooperateurs")
                .then()
                .statusCode(200)
                .body("find { it.id == " + id + " }.status", equalTo("PROCESSED"));
    }
}