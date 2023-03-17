package pl.patrykbober.bloomer.auth.model;

public record RefreshTokenAuthRequest(
        String refreshToken
) {
}
