package pl.patrykbober.bloomer.model.response;

import java.time.Instant;

public record AccessTokenResponse(
        String accessToken,
        Instant accessTokenExpiresAt,
        String refreshToken,
        Instant refreshTokenExpiresAt
) {
}
