package org.sqq.registration.matrix;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.sqq.registration.Cooperateur;
import org.sqq.registration.SouscriptionSupplementaire;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@ApplicationScoped
public class MatrixNotificationService {

    private final MatrixConfiguration config;
    private final MatrixClient matrixClient;

    public MatrixNotificationService(MatrixConfiguration config, @RestClient MatrixClient matrixClient) {
        this.config = config;
        this.matrixClient = matrixClient;
    }

    public void notifyNewSubscription(Cooperateur cooperateur) {
        if (!isEnabled()) {
            Log.debug("Matrix notifications disabled, skipping");
            return;
        }

        long amount = cooperateur.parts * config.pricePerShare();
        String message = formatNewSubscriptionMessage(cooperateur, amount);

        sendMessage(message, "cooperateur-" + cooperateur.id);
    }

    public void notifyAdditionalShares(SouscriptionSupplementaire souscription) {
        if (!isEnabled()) {
            Log.debug("Matrix notifications disabled, skipping");
            return;
        }

        long amount = souscription.partsSupplementaires * config.pricePerShare();
        String message = formatAdditionalSharesMessage(souscription, amount);

        sendMessage(message, "souscription-" + souscription.id);
    }

    private boolean isEnabled() {
        return config.enabled()
                && config.homeserverUrl().isPresent()
                && config.accessToken().isPresent()
                && config.roomId().isPresent();
    }

    private void sendMessage(String message, String contextId) {
        String txnId = contextId + "-" + UUID.randomUUID();
        String authHeader = "Bearer " + config.accessToken().orElseThrow();
        String roomId = config.roomId().orElseThrow();

        CompletableFuture.runAsync(() -> {
            try {
                matrixClient.sendMessage(
                        roomId,
                        txnId,
                        authHeader,
                        MatrixMessageRequest.text(message)
                );
                Log.infof("Matrix notification sent: %s", contextId);
            } catch (Exception e) {
                Log.errorf(e, "Failed to send Matrix notification for %s", contextId);
            }
        });
    }
    private String formatNewSubscriptionMessage(Cooperateur cooperateur, long amount) {
        return String.format(
                "Nouveau paiement recu!\n" +
                "Nom: %s %s\n" +
                "Email: %s\n" +
                "Parts sociales: %d\n" +
                "Montant: %d EUR",
                cooperateur.prenom,
                cooperateur.nom,
                cooperateur.email,
                cooperateur.parts,
                amount
        );
    }

    private String formatAdditionalSharesMessage(SouscriptionSupplementaire souscription, long amount) {
        return String.format(
                "Nouveau paiement de parts supplementaires!\n" +
                "Nom: %s %s\n" +
                "Email: %s\n" +
                "Parts supplementaires: %d\n" +
                "Montant: %d EUR",
                souscription.prenom,
                souscription.nom,
                souscription.email,
                souscription.partsSupplementaires,
                amount
        );
    }
}
