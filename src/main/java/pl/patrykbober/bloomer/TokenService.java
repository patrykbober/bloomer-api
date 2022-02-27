package pl.patrykbober.bloomer;

import lombok.RequiredArgsConstructor;
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

    private final static long ACCESS_TOKEN_EXPIRY = 3600L;
    private final static long REFRESH_TOKEN_EXPIRY = 30L;

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
                .expiresAt(now.plusSeconds(ACCESS_TOKEN_EXPIRY))
                .subject(username)
                .claim("scope", scope)
                .build();
        var accessToken = this.encoder.encode(JwtEncoderParameters.from(accessTokenClaims)).getTokenValue();

        var refreshTokenClaims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(REFRESH_TOKEN_EXPIRY))
                .subject(username)
                .build();
        var refreshToken = this.encoder.encode(JwtEncoderParameters.from(refreshTokenClaims)).getTokenValue();

        return AccessTokenResponse.builder()
                .accessToken(accessToken)
                .accessTokenExpiresAt(now.plusSeconds(ACCESS_TOKEN_EXPIRY))
                .refreshToken(refreshToken)
                .refreshTokenExpiresAt(now.plusSeconds(REFRESH_TOKEN_EXPIRY))
                .build();
    }
}
