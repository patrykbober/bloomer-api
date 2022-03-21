package pl.patrykbober.bloomer.auth;

import java.time.Instant;

public record AccessTokenResponse(
        String accessToken,
        Instant accessTokenExpiresAt,
        String refreshToken,
        Instant refreshTokenExpiresAt
) {
}
