package pl.patrykbober.bloomer.domain.user.request;

import java.util.List;

public record UpdateUserRequest(
        String firstName,
        String lastName,
        String password,
        Boolean active,
        List<String> roles
) {
}
