package pl.patrykbober.bloomer.auth.keycloak;

import lombok.RequiredArgsConstructor;
import pl.patrykbober.bloomer.auth.AuthenticationFacade;
import pl.patrykbober.bloomer.auth.keycloak.dto.KeycloakTokenRequest;
import pl.patrykbober.bloomer.auth.model.AuthResponse;
import pl.patrykbober.bloomer.auth.model.CredentialsAuthRequest;
import pl.patrykbober.bloomer.auth.model.RefreshTokenAuthRequest;

@RequiredArgsConstructor
class KeycloakAuthenticationService implements AuthenticationFacade {

    private static final String PASSWORD_GRANT_TYPE = "password";
    private static final String REFRESH_TOKEN_GRANT_TYPE = "refresh_token";

    private final KeycloakClient keycloakClient;
    private final String clientId;

    @Override
    public AuthResponse login(CredentialsAuthRequest authRequest) {
        var keycloakTokenRequest = KeycloakTokenRequest.ofCredentials(PASSWORD_GRANT_TYPE, clientId, authRequest.username(), authRequest.password());
        var keycloakTokenResponse = keycloakClient.token(keycloakTokenRequest);
        return keycloakTokenResponse.toAuthResponse();
    }

    @Override
    public AuthResponse refresh(RefreshTokenAuthRequest authRequest) {
        var keycloakTokenRequest = KeycloakTokenRequest.ofRefreshToken(REFRESH_TOKEN_GRANT_TYPE, clientId, authRequest.refreshToken());
        var keycloakTokenResponse = keycloakClient.token(keycloakTokenRequest);
        return keycloakTokenResponse.toAuthResponse();
    }

}
