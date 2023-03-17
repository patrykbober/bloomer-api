package pl.patrykbober.bloomer.auth.api;

import pl.patrykbober.bloomer.auth.model.RefreshTokenAuthRequest;

record RefreshTokenAuthRequestDto(
        String refreshToken
) {

    RefreshTokenAuthRequest toAuthRequest() {
        return new RefreshTokenAuthRequest(refreshToken);
    }

}
