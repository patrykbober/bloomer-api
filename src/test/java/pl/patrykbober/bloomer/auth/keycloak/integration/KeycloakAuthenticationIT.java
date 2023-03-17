package pl.patrykbober.bloomer.auth.keycloak.integration;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class KeycloakAuthenticationIT {

    @Container
    private static final KeycloakContainer keycloak = new KeycloakContainer("quay.io/keycloak/keycloak:21.0")
            .withRealmImportFile("keycloak/bloomer-realm-export.json");

    @Autowired
    private WebTestClient webTestClient;

    @DynamicPropertySource
    static void registerResourceServerIssuerProperty(DynamicPropertyRegistry registry) {
        registry.add("app.auth.keycloak.base-url", keycloak::getAuthServerUrl);
    }

    @Test
    void loginAndRefreshTokenTest() {
        var loginResponse = webTestClient
                .post()
                .uri("/v1/auth/login")
                .bodyValue("""
                        {
                         "username": "user@bloomer.com",
                         "password": "user"
                        }
                         """)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isOk()
                .returnResult(new ParameterizedTypeReference<Map<String, String>>() {
                })
                .getResponseBody()
                .blockFirst();

        assertThat(loginResponse)
                .isNotNull()
                .containsKey("refreshToken");

        var refreshResponse = webTestClient
                .post()
                .uri("/v1/auth/refresh")
                .bodyValue(String.format("""
                        {
                         "refreshToken": "%s"
                        }
                         """, loginResponse.get("refreshToken")))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isOk()
                .returnResult(new ParameterizedTypeReference<Map<String, String>>() {
                })
                .getResponseBody()
                .blockFirst();

        assertThat(refreshResponse)
                .isNotNull()
                .extracting(map -> map.get("accessToken")).isNotEqualTo(loginResponse.get("accessToken"));
    }

    @Test
    void refreshExpiredTokenTest() throws InterruptedException {
        var loginResponse = webTestClient
                .post()
                .uri("/v1/auth/login")
                .bodyValue("""
                        {
                         "username": "user@bloomer.com",
                         "password": "user"
                        }
                         """)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isOk()
                .returnResult(new ParameterizedTypeReference<Map<String, String>>() {
                })
                .getResponseBody()
                .blockFirst();

        assertThat(loginResponse)
                .isNotNull()
                .containsKey("refreshToken");

        // wait for refresh token to expire
        Thread.sleep(3000);

        webTestClient
                .post()
                .uri("/v1/auth/refresh")
                .bodyValue(String.format("""
                        {
                         "refreshToken": "%s"
                        }
                         """, loginResponse.get("refreshToken")))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Unauthorized")
                .jsonPath("$.message").isEqualTo("Token is not active");
    }

    @Test
    void incorrectCredentialsLoginTest() {
        webTestClient
                .post()
                .uri("/v1/auth/login")
                .bodyValue("""
                        {
                         "username": "user@bloomer.com",
                         "password": "invalid"
                        }
                         """)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Unauthorized")
                .jsonPath("$.message").isEqualTo("Invalid user credentials");
    }

    @Test
    void invalidRefreshTokenTest() {
        webTestClient
                .post()
                .uri("/v1/auth/refresh")
                .bodyValue("""
                        {
                         "refreshToken": "invalid-refresh-token"
                        }
                         """)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Unauthorized")
                .jsonPath("$.message").isEqualTo("Invalid refresh token");
    }

}
