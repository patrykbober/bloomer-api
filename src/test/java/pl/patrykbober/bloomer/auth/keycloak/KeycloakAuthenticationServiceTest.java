package pl.patrykbober.bloomer.auth.keycloak;

import org.junit.jupiter.api.Test;
import pl.patrykbober.bloomer.auth.exception.AuthenticationFailedException;
import pl.patrykbober.bloomer.auth.keycloak.dto.KeycloakTokenResponse;
import pl.patrykbober.bloomer.auth.model.AuthResponse;
import pl.patrykbober.bloomer.auth.model.CredentialsAuthRequest;
import pl.patrykbober.bloomer.auth.model.RefreshTokenAuthRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class KeycloakAuthenticationServiceTest {

    private final KeycloakClient keycloakClient = mock(KeycloakClient.class);
    private final KeycloakAuthenticationService authenticationService = new KeycloakAuthenticationService(keycloakClient, "bloomer-test-client");

    @Test
    void shouldReturnAuthResponseWhenValidCredentialsGiven() {
        // given
        var authRequest = new CredentialsAuthRequest("valid-username", "valid-password");

        var expectedKeycloakTokenResponse = new KeycloakTokenResponse("accessToken", 300, "refreshToken", 1800, "scope");
        when(keycloakClient.token(any())).thenReturn(expectedKeycloakTokenResponse);

        // when
        var authResponse = authenticationService.login(authRequest);

        // then
        assertThat(authResponse)
                .returns("accessToken", AuthResponse::accessToken)
                .returns(300, AuthResponse::expiresIn)
                .returns("refreshToken", AuthResponse::refreshToken)
                .returns(1800, AuthResponse::refreshExpiresIn);
    }

    @Test
    void shouldThrowExceptionWhenInvalidCredentialsGiven() {
        // given
        var authRequest = new CredentialsAuthRequest("invalid-username", "invalid-password");
        doThrow(new AuthenticationFailedException("Invalid user credentials")).when(keycloakClient).token(any());

        // when

        // then
        assertThatThrownBy(() -> authenticationService.login(authRequest))
                .isInstanceOf(AuthenticationFailedException.class);
    }

    @Test
    void shouldReturnAuthResponseWhenValidRefreshTokenGiven() {
        // given
        var authRequest = new RefreshTokenAuthRequest("valid-refresh-token");

        var expectedKeycloakTokenResponse = new KeycloakTokenResponse("accessToken", 300, "refreshToken", 1800, "scope");
        when(keycloakClient.token(any())).thenReturn(expectedKeycloakTokenResponse);

        // when
        var authResponse = authenticationService.refresh(authRequest);

        // then
        assertThat(authResponse)
                .returns("accessToken", AuthResponse::accessToken)
                .returns(300, AuthResponse::expiresIn)
                .returns("refreshToken", AuthResponse::refreshToken)
                .returns(1800, AuthResponse::refreshExpiresIn);
    }

    @Test
    void shouldThrowExceptionWhenInvalidRefreshTokenGiven() {
        // given
        var authRequest = new RefreshTokenAuthRequest("invalid-refresh-token");
        doThrow(new AuthenticationFailedException("Invalid refresh token")).when(keycloakClient).token(any());

        // when

        // then
        assertThatThrownBy(() -> authenticationService.refresh(authRequest))
                .isInstanceOf(AuthenticationFailedException.class);
    }

}