package pl.patrykbober.bloomer.domain.user;

public record UserDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        Boolean active
) {
}
