package pl.patrykbober.bloomer.user.response;

import pl.patrykbober.bloomer.user.dto.RoleDto;

import java.util.List;

public record RolesResponse(
        List<RoleDto> roles
) {
}
