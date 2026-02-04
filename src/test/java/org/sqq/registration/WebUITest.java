package org.sqq.registration;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.AriaRole;
import com.stripe.exception.StripeException;
import io.quarkiverse.playwright.InjectPlaywright;
import io.quarkiverse.playwright.WithPlaywright;
import io.quarkiverse.quinoa.testing.QuinoaTestProfiles;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sqq.registration.stripe.Stripe;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@QuarkusTest
@TestProfile(QuinoaTestProfiles.Enable.class)
@WithPlaywright
public class WebUITest {
    @InjectPlaywright
    BrowserContext context;

    @TestHTTPResource("/")
    URL url;

    @BeforeAll
    public static void setup() throws StripeException, URISyntaxException {
        Stripe mockedStripe = Mockito.mock(Stripe.class);
        Mockito.when(mockedStripe.paySouscription(ArgumentMatchers.any())).thenAnswer(invocation -> {
            Cooperateur cooperateur = invocation.getArgument(0);
            return new URI("http://localhost:8081/payment-result?uuid=" + cooperateur.uuid);
        });
        Mockito.when(mockedStripe.hasPaid(ArgumentMatchers.any())).thenReturn(true);
        Mockito.when(mockedStripe.paySouscriptionSupplementaire(ArgumentMatchers.any())).thenAnswer(invocation -> {
            SouscriptionSupplementaire souscription = invocation.getArgument(0);
            return new URI("http://localhost:8081/payment-result?uuid=" + souscription.uuid + "&type=supplementaire");
        });

        Mockito.when(mockedStripe.hasPaidSouscriptionSupplementaire(ArgumentMatchers.any())).thenReturn(true);
        QuarkusMock.installMockForType(mockedStripe, Stripe.class);
    }

    @Test
    void shouldRegisterCooperateur() {
        final Page page = context.newPage();
        Response response = page.navigate(url.toString());
        Assertions.assertEquals("OK", response.statusText());

        page.getByRole(AriaRole.RADIO, new Page.GetByRoleOptions().setName("Monsieur")).check();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Prénom *")).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Prénom *")).fill("Jean");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Nom *").setExact(true)).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Nom *").setExact(true)).fill("Michel");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Adresse *")).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Adresse *")).fill("20 rue des Lilas");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Ville *")).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Ville *")).fill("Lille");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Ville *")).press("Tab");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Code postal *")).fill("59000");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email *")).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email *")).fill("jean.michel@internet.com");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Téléphone *")).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Téléphone *")).fill("0736473829");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Date de naissance *")).fill("2025-12-25");
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("€ et plus")).check();
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Je déclare avoir pris")).check();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Payer 100.00 €")).click();

        page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Merci pour ta souscription !")).waitFor();

    }

    @Test
    void shouldSubscribeAdditionalShares() {
        final Page page = context.newPage();
        
        Response response = page.navigate(url.toString() + "parts-supplementaires");
        Assertions.assertEquals("OK", response.statusText());

        page.locator("#prenom").fill("Marie");
        page.locator("#nom").fill("Dupont");
        page.locator("#email").fill("marie.dupont@exemple.fr");

        // Increase additional shares using the stepper button
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("+")).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("+")).click();

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Payer 30.00 €")).click();

        page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Merci pour ta souscription !")).waitFor();
    }

    @Test
    void shouldArchiveCooperateurFromAdminPage() throws URISyntaxException {
        final Page page = context.newPage();

        String pendingEmail = "pending.test." + System.currentTimeMillis() + "@test.com";

        // 1. Create a cooperateur via API (stays in PAYMENT_PENDING because we don't call success endpoint)
        page.request().post(url.toString() + "api/v1/registrations",
            com.microsoft.playwright.options.RequestOptions.create()
                .setForm(com.microsoft.playwright.options.FormData.create()
                    .set("genre", "MONSIEUR")
                    .set("prenom", "Pending")
                    .set("nom", "Test")
                    .set("adresse", "2 rue Test")
                    .set("ville", "Lille")
                    .set("codePostal", "59000")
                    .set("email", pendingEmail)
                    .set("telephone", "0600000001")
                    .set("etudiantOuMinimasSociaux", "false")
                    .set("nombreDePersonnesDansLeFoyer", "1")
                    .set("partsDeSoutien", "0")
                    .set("acceptationDesStatus", "true")
                    .set("binomeEnabled", "false")));

        // 2. Navigate to admin page with basic auth in URL
        URI adminUri = new URI(url.getProtocol(), "admin:password", url.getHost(), url.getPort(), "/admin", null, null);
        page.navigate(adminUri.toString());

        // 3. Wait for the table to load
        page.waitForSelector("table");

        // Search for our PAYMENT_PENDING cooperateur
        page.getByPlaceholder("Rechercher").first().fill(pendingEmail);
        page.waitForTimeout(500);

        // Verify the cooperateur is visible with PAYMENT_PENDING status
        Assertions.assertTrue(page.locator("tr:has-text('" + pendingEmail + "') .status.pending").isVisible(),
            "Cooperateur should have 'Paiement en attente' status");

        // 4. Click the Archive button and accept confirmation
        page.onDialog(dialog -> dialog.accept());
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Archiver")).click();

        // 5. Wait and verify cooperateur is no longer visible (default filter excludes ARCHIVED)
        page.waitForTimeout(1000);
        page.getByPlaceholder("Rechercher").first().fill(pendingEmail);
        page.waitForTimeout(500);
        Assertions.assertFalse(page.locator("td:has-text('" + pendingEmail + "')").isVisible(),
            "Archived cooperateur should not be visible with default filter");

        // 6. Check the ARCHIVED filter checkbox to include archived items
        page.locator("input[type='checkbox'][value='ARCHIVED']").first().check();
        page.waitForTimeout(1000); // Wait for API reload

        // 7. Search and verify the cooperateur is visible with ARCHIVED status
        page.getByPlaceholder("Rechercher").first().fill(pendingEmail);
        page.waitForTimeout(500);
        Assertions.assertTrue(page.locator("td:has-text('" + pendingEmail + "')").isVisible(),
            "Archived cooperateur should be visible when ARCHIVED filter is enabled");
        // Verify the status within the same row as our email
        Assertions.assertTrue(page.locator("tr:has-text('" + pendingEmail + "') .status.archived").isVisible(),
            "Cooperateur should have 'Archivée' status");
    }
}
