package pl.patrykbober.bloomer.auth.keycloak;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.auth.keycloak")
record KeycloakAuthenticationProperties(
        String baseUrl,
        String realmName,
        String clientId
) {
}
