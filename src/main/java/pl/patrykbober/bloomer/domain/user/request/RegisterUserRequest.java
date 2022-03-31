package pl.patrykbober.bloomer.domain.user.request;

public record RegisterUserRequest(
        String email,
        String firstName,
        String lastName,
        String password
) {
}
