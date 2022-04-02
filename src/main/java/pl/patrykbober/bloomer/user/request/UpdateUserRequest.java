package pl.patrykbober.bloomer.user.request;

import java.util.List;

public record UpdateUserRequest(
        String firstName,
        String lastName,
        String password,
        Boolean active,
        List<String> roles
) {
}
