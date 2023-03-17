package pl.patrykbober.bloomer.auth.model;

public record CredentialsAuthRequest(
        String username,
        String password
) {
}
