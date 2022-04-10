package pl.patrykbober.bloomer.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import pl.patrykbober.bloomer.common.exception.BloomerException;
import pl.patrykbober.bloomer.common.exception.ErrorCode;
import pl.patrykbober.bloomer.user.dto.RoleDto;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleService roleService;

    private final RoleMapper mapper = Mappers.getMapper(RoleMapper.class);

    @Test
    void getAllRolesFromDatabase() {
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

    @Test
    void addRolesToUser() {
        // given
        var userId = 1L;
        var roleNames = List.of("admin ", "newRole");

        var userRole = new Role(1L, "USER", true);
        var adminRole = new Role(2L, "ADMIN", false);
        var newRole = new Role(3L, "NEWROLE", false);

        var user = BloomerUser.builder()
                .roles(new HashSet<>(List.of(userRole)))
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(roleRepository.findByNameIn(any())).thenReturn(List.of(adminRole, newRole));

        // when
        roleService.addRolesToUser(userId, roleNames);

        // then
        verify(userRepository).save(any(BloomerUser.class));
        assertThat(user.getRoles())
                .hasSize(3)
                .containsAll(List.of(userRole, adminRole, newRole));
    }

    @Test
    void throwExceptionWhenAddRolesInvokedForUserWithIdNotFoundInDatabase() {
        // given
        var userId = 1L;
        var roleNames = List.of("admin ", "newRole");
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // when
        var thrown = catchThrowableOfType(() -> roleService.addRolesToUser(userId, roleNames), BloomerException.class);

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void deleteRolesFromUser() {
        // given
        var userId = 1L;
        var roleNames = List.of("admin ");

        var adminRole = new Role(1L, "ADMIN", true);
        var userRole = new Role(2L, "USER", false);

        var user = BloomerUser.builder()
                .roles(new HashSet<>(List.of(userRole, adminRole)))
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(roleRepository.findByNameIn(any())).thenReturn(List.of(adminRole));

        // when
        roleService.deleteRolesFromUser(userId, roleNames);

        // then
        verify(userRepository).save(any(BloomerUser.class));
        assertThat(user.getRoles())
                .hasSize(1)
                .element(0)
                .returns("USER", Role::getName);
    }

    @Test
    void throwExceptionWhenDeleteRolesInvokedForUserWithIdNotFoundInDatabase() {
        // given
        var userId = 1L;
        var roleNames = List.of("admin ", "newRole");
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // when
        var thrown = catchThrowableOfType(() -> roleService.deleteRolesFromUser(userId, roleNames), BloomerException.class);

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    private Answer<RoleDto> mapInputToDto() {
        return i -> mapper.toDto(i.getArgument(0, Role.class));
    }

}
