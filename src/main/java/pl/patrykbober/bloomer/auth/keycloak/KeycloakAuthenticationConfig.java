package pl.patrykbober.bloomer.auth.keycloak;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import pl.patrykbober.bloomer.auth.AuthenticationFacade;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(KeycloakAuthenticationProperties.class)
class KeycloakAuthenticationConfig {

    private final KeycloakAuthenticationProperties authenticationProperties;

    @Bean
    AuthenticationFacade authenticationFacade() {
        var webClient = WebClient.builder()
                .baseUrl(authenticationProperties.baseUrl())
                .defaultUriVariables(Map.of("realm", authenticationProperties.realmName()))
                .build();
        var keycloakClient = new WebClientKeycloakClient(webClient);
        return new KeycloakAuthenticationService(keycloakClient, authenticationProperties.clientId());
    }

}
