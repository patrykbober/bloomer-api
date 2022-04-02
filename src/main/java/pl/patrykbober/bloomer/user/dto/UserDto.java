package pl.patrykbober.bloomer.user.dto;

public record UserDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        Boolean active
) {
}
