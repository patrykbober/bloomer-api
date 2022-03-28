package pl.patrykbober.bloomer.domain.token;

import java.time.Instant;

public record AccessTokenResponse(
        String accessToken,
        Instant accessTokenExpiresAt,
        String refreshToken,
        Instant refreshTokenExpiresAt
) {
}
