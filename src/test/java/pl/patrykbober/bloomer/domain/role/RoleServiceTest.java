package pl.patrykbober.bloomer.domain.role;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import pl.patrykbober.bloomer.domain.role.dto.RoleDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private final RoleMapper mapper = Mappers.getMapper(RoleMapper.class);

    @Test
    public void getAllRolesFromDatabase() {
        // given
        var userRole = Role.builder()
                .id(1L)
                .name("USER")
                .build();
        var adminRole = Role.builder()
                .id(2L)
                .name("ADMIN")
                .build();

        when(roleRepository.findAll()).thenReturn(List.of(userRole, adminRole));
        when(roleMapper.toDto(any(Role.class))).thenAnswer(mapInputToDto());

        // when
        var result = roleService.findAll();

        // then
        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .anyMatch(role -> role.id().equals(1L) && role.name().equals("USER"))
                .anyMatch(role -> role.id().equals(2L) && role.name().equals("ADMIN"));
    }

    private Answer<RoleDto> mapInputToDto() {
        return i -> mapper.toDto(i.getArgument(0, Role.class));
    }

}
