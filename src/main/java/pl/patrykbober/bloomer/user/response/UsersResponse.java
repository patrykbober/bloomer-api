package pl.patrykbober.bloomer.user.response;

import pl.patrykbober.bloomer.user.dto.UserDto;

import java.util.List;

public record UsersResponse(
        List<UserDto> users
) {
}
