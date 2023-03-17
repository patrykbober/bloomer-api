package pl.patrykbober.bloomer.auth.api;

import pl.patrykbober.bloomer.auth.model.AuthResponse;

record AuthResponseDto(
        String accessToken,
        int expiresIn,
        String refreshToken,
        int refreshExpiresIn
) {

    static AuthResponseDto ofAuthResponse(AuthResponse authResponse) {
        return new AuthResponseDto(authResponse.accessToken(),
                authResponse.expiresIn(),
                authResponse.refreshToken(),
                authResponse.refreshExpiresIn());
    }

}
