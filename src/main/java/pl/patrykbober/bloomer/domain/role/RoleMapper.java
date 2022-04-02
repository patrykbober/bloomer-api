package pl.patrykbober.bloomer.domain.role;

import org.mapstruct.Mapper;
import pl.patrykbober.bloomer.domain.role.dto.RoleDto;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleDto toDto(Role role);

}
