package pl.patrykbober.bloomer.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${app.jwt.expiry.access_token}")
    private long accessTokenExpiry;

    @Value("${app.jwt.expiry.refresh_token}")
    private long refreshTokenExpiry;

    private final JwtEncoder encoder;

    public AccessTokenResponse getAccessTokenResponse(Authentication authentication) {
        var username = authentication.getName();

        var now = Instant.now();
        var scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        var accessTokenClaims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(accessTokenExpiry))
                .subject(username)
                .claim("scope", scope)
                .build();
        var accessToken = this.encoder.encode(JwtEncoderParameters.from(accessTokenClaims)).getTokenValue();

        var refreshTokenClaims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(refreshTokenExpiry))
                .subject(username)
                .build();
        var refreshToken = this.encoder.encode(JwtEncoderParameters.from(refreshTokenClaims)).getTokenValue();

        return new AccessTokenResponse(
                accessToken,
                now.plusSeconds(accessTokenExpiry),
                refreshToken,
                now.plusSeconds(refreshTokenExpiry)
        );
    }
}
