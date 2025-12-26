package org.sqq.registration.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Request DTO for creating a pair (principal + binôme)")
public class CreatePairV2Request {

    @NotNull
    @Valid
    @Schema(description = "Données du membre principal")
    public CreateCooperateurV2Request principal;

    @NotNull
    @Valid
    @Schema(description = "Données du binôme")
    public CreateBinomeV2Request binome;

    public CreatePairV2Request() {
    }
}
