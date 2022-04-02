package pl.patrykbober.bloomer.user.request;

public record RegisterUserRequest(
        String email,
        String firstName,
        String lastName,
        String password
) {
}
