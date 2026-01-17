package org.sqq.registration.matrix;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MatrixMessageRequest(
        @JsonProperty("msgtype") String msgType,
        @JsonProperty("body") String body
) {
    public static MatrixMessageRequest text(String body) {
        return new MatrixMessageRequest("m.text", body);
    }
}
