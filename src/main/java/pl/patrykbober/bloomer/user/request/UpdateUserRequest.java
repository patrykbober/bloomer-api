package pl.patrykbober.bloomer.user.request;

public record UpdateUserRequest(
        String firstName,
        String lastName,
        String password,
        Boolean active
) {
}
