package pl.patrykbober.bloomer.user;

import org.mapstruct.Mapper;
import pl.patrykbober.bloomer.user.dto.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(BloomerUser user);

}
