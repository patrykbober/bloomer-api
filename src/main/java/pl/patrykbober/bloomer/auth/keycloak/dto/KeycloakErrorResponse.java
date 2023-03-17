package pl.patrykbober.bloomer.auth.keycloak.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KeycloakErrorResponse(
        String error,
        @JsonProperty("error_description")
        String errorDescription
) {
}
