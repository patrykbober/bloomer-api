package pl.patrykbober.bloomer.domain.user.request;

public record SelfUpdateUserRequest(
        String firstName,
        String lastName
) {
}
