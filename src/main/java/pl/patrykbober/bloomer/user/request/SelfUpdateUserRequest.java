package pl.patrykbober.bloomer.user.request;

public record SelfUpdateUserRequest(
        String firstName,
        String lastName
) {
}
