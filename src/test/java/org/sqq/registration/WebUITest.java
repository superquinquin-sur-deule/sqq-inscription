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
        Mockito.when(mockedStripe.paySouscription(ArgumentMatchers.any())).thenReturn(new URI("http://localhost:8081/success"));
        Mockito.when(mockedStripe.hasPaid(ArgumentMatchers.any())).thenReturn(true);
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
}
