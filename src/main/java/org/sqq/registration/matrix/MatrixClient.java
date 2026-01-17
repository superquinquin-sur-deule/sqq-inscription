package org.sqq.registration.matrix;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "matrix-api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface MatrixClient {

    @PUT
    @Path("/_matrix/client/v3/rooms/{roomId}/send/m.room.message/{txnId}")
    void sendMessage(
            @PathParam("roomId") String roomId,
            @PathParam("txnId") String txnId,
            @HeaderParam("Authorization") String authorization,
            MatrixMessageRequest message
    );
}
