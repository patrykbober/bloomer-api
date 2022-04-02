package pl.patrykbober.bloomer.user;

import org.mapstruct.Mapper;
import pl.patrykbober.bloomer.user.dto.RoleDto;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleDto toDto(Role role);

}
