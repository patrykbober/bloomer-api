package pl.patrykbober.bloomer.auth.keycloak.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Optional;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class KeycloakTokenRequest {
    String grantType;
    String clientId;
    String username;
    String password;
    String refreshToken;

    public static KeycloakTokenRequest ofCredentials(String grantType, String clientId, String username, String password) {
        return new KeycloakTokenRequest(grantType, clientId, username, password, null);
    }

    public static KeycloakTokenRequest ofRefreshToken(String grantType, String clientId, String refreshToken) {
        return new KeycloakTokenRequest(grantType, clientId, null, null, refreshToken);
    }

    public MultiValueMap<String, String> toMultiValueMap() {
        var multiValueMap = new LinkedMultiValueMap<String, String>();
        multiValueMap.add("grant_type", grantType);
        multiValueMap.add("client_id", clientId);
        Optional.ofNullable(username).ifPresent(username -> multiValueMap.add("username", username));
        Optional.ofNullable(password).ifPresent(password -> multiValueMap.add("password", password));
        Optional.ofNullable(refreshToken).ifPresent(refreshToken -> multiValueMap.add("refresh_token", refreshToken));
        return multiValueMap;
    }

}
