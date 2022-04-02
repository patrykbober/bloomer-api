package pl.patrykbober.bloomer.domain.role.response;

import pl.patrykbober.bloomer.domain.role.dto.RoleDto;

import java.util.List;

public record RolesResponse(
        List<RoleDto> roles
) {
}
