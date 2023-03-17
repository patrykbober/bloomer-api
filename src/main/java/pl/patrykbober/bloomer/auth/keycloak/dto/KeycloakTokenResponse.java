package pl.patrykbober.bloomer.auth.keycloak.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import pl.patrykbober.bloomer.auth.model.AuthResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KeycloakTokenResponse(
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("expires_in")
        int expiresIn,
        @JsonProperty("refresh_token")
        String refreshToken,
        @JsonProperty("refresh_expires_in")
        int refreshExpiresIn,
        String scope
) {

    public AuthResponse toAuthResponse() {
        return new AuthResponse(accessToken, expiresIn, refreshToken, refreshExpiresIn);
    }

}
