package pl.patrykbober.bloomer.common.security;

import java.time.Instant;

public record AccessTokenResponse(
        String accessToken,
        Instant accessTokenExpiresAt,
        String refreshToken,
        Instant refreshTokenExpiresAt
) {
}
