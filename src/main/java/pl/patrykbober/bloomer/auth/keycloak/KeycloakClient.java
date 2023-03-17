package pl.patrykbober.bloomer.auth.keycloak;

import pl.patrykbober.bloomer.auth.keycloak.dto.KeycloakTokenRequest;
import pl.patrykbober.bloomer.auth.keycloak.dto.KeycloakTokenResponse;

interface KeycloakClient {

    KeycloakTokenResponse token(KeycloakTokenRequest request);

}
