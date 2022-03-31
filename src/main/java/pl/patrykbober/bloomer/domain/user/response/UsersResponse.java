package pl.patrykbober.bloomer.domain.user.response;

import pl.patrykbober.bloomer.domain.user.UserDto;

import java.util.List;

public record UsersResponse(
        List<UserDto> users
) {
}
