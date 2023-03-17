package pl.patrykbober.bloomer.auth.keycloak;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import pl.patrykbober.bloomer.auth.exception.AuthenticationFailedException;
import pl.patrykbober.bloomer.auth.keycloak.dto.KeycloakErrorResponse;
import pl.patrykbober.bloomer.auth.keycloak.dto.KeycloakTokenRequest;
import pl.patrykbober.bloomer.auth.keycloak.dto.KeycloakTokenResponse;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
class WebClientKeycloakClient implements KeycloakClient {

    private static final String KEYCLOAK_TOKEN_ENDPOINT_URI = "/realms/{realm}/protocol/openid-connect/token";

    private final WebClient client;

    @Override
    public KeycloakTokenResponse token(KeycloakTokenRequest request) {
        return client.post()
                .uri(uriBuilder -> uriBuilder.path(KEYCLOAK_TOKEN_ENDPOINT_URI).build())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(request.toMultiValueMap()))
                .retrieve()
                .onStatus(HttpStatus::isError, response ->
                        response.bodyToMono(KeycloakErrorResponse.class)
                                .flatMap(errorResponse -> Mono.error(new AuthenticationFailedException(errorResponse.errorDescription()))))
                .bodyToMono(KeycloakTokenResponse.class)
                .block();
    }

}
