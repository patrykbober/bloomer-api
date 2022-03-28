package pl.patrykbober.bloomer.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Sql(value = "/sql/users_data.sql")
@Transactional
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class AuthIntegrationTest {

    public static final String TOKEN_ENDPOINT = "/token";

    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${app.jwt.expiry.access_token}")
    private long accessTokenExpiry;

    @Value("${app.jwt.expiry.refresh_token}")
    private long refreshTokenExpiry;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Test
    public void loginWithValidCredentialsReturnsCorrectResponse() throws Exception {
        var grant_type = "password";
        var username = "user@bloomer.com";
        var password = "password";
        var mvcResult = mockMvc.perform(post(TOKEN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("grant_type", grant_type)
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isOk())
                .andReturn();

        var responseMap = mapper.readValue(mvcResult.getResponse().getContentAsString(), Map.class);
        var accessToken = jwtDecoder.decode((String) responseMap.get("accessToken"));

        assertThat(accessToken).isNotNull();
        assertThat(accessToken.getIssuedAt()).isNotNull()
                .isCloseTo(Instant.now(), within(3, ChronoUnit.SECONDS));
        assertThat(accessToken.getExpiresAt()).isNotNull()
                .isCloseTo(Instant.now().plusSeconds(accessTokenExpiry), within(3, ChronoUnit.SECONDS));
        assertThat(accessToken.getExpiresAt()).isCloseTo((String) responseMap.get("accessTokenExpiresAt"), within(1, ChronoUnit.SECONDS));
        assertThat(accessToken.hasClaim("iss")).isTrue();
        assertThat(accessToken.getClaimAsString("iss")).isEqualTo("self");
        assertThat(accessToken.getSubject()).isNotNull().isEqualTo(username);
        assertThat(accessToken.hasClaim("roles")).isTrue();
        assertThat(accessToken.getClaimAsStringList("roles")).isNotNull().hasSize(1).containsExactly("USER");

        var refreshToken = jwtDecoder.decode((String) responseMap.get("refreshToken"));

        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken.getIssuedAt()).isNotNull()
                .isCloseTo(Instant.now(), within(3, ChronoUnit.SECONDS));
        assertThat(refreshToken.getExpiresAt()).isNotNull()
                .isCloseTo(Instant.now().plusSeconds(refreshTokenExpiry), within(3, ChronoUnit.SECONDS));
        assertThat(refreshToken.getExpiresAt()).isCloseTo((String) responseMap.get("refreshTokenExpiresAt"), within(1, ChronoUnit.SECONDS));
        assertThat(refreshToken.hasClaim("iss")).isTrue();
        assertThat(refreshToken.getClaimAsString("iss")).isEqualTo("self");
        assertThat(refreshToken.getSubject()).isNotNull().isEqualTo(username);
    }

    @Test
    public void loginWithInvalidUsernameResultsIn401() throws Exception {
        var grant_type = "password";
        var username = "invalid_user@bloomer.com";
        var password = "password";

        mockMvc.perform(post(TOKEN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("grant_type", grant_type)
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void loginWithInvalidGrantTypeResultsIn400() throws Exception {
        var grant_type = "invalid";
        var username = "user@bloomer.com";
        var password = "password";

        mockMvc.perform(post(TOKEN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("grant_type", grant_type)
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void loginWithInvalidPasswordResultsIn401() throws Exception {
        var grant_type = "password";
        var username = "user@bloomer.com";
        var password = "invalid_password";

        mockMvc.perform(post(TOKEN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("grant_type", grant_type)
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void refreshWithValidRefreshTokenReturnsCorrectResponseWithNewRefreshToken() throws Exception {
        var username = "admin@bloomer.com";
        var password = "password";

        var mvcResultLogin = mockMvc.perform(post(TOKEN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("grant_type", "password")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isOk())
                .andReturn();

        var loginResponseMap = mapper.readValue(mvcResultLogin.getResponse().getContentAsString(), Map.class);
        var refreshToken = (String) loginResponseMap.get("refreshToken");

        // wait a second for new refresh token issuedAt to be different
        Thread.sleep(1000);

        var mvcResultRefresh = mockMvc.perform(post(TOKEN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("grant_type", "refresh_token")
                        .param("refresh_token", refreshToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andReturn();

        var refreshResponseMap = mapper.readValue(mvcResultRefresh.getResponse().getContentAsString(), Map.class);
        var newRefreshToken = (String) refreshResponseMap.get("refreshToken");

        assertThat(newRefreshToken).isNotEqualTo(refreshToken);
    }

}
