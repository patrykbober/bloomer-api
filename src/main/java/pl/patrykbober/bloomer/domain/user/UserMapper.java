package pl.patrykbober.bloomer.domain.user;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(BloomerUser user);

}
