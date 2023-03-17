package pl.patrykbober.bloomer.auth.model;

public record AuthResponse(
        String accessToken,
        int expiresIn,
        String refreshToken,
        int refreshExpiresIn
) {
}
