package pl.patrykbober.bloomer.auth.api;

import pl.patrykbober.bloomer.auth.model.CredentialsAuthRequest;

record CredentialsAuthRequestDto(
        String username,
        String password
) {

    CredentialsAuthRequest toAuthRequest() {
        return new CredentialsAuthRequest(username, password);
    }

}
