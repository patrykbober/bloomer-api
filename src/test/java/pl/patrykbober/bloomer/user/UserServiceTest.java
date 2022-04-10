package pl.patrykbober.bloomer.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.patrykbober.bloomer.common.exception.BloomerException;
import pl.patrykbober.bloomer.common.exception.ErrorCode;
import pl.patrykbober.bloomer.user.dto.UserDto;
import pl.patrykbober.bloomer.user.event.OnUserRegistrationCompleteEvent;
import pl.patrykbober.bloomer.user.request.CreateUserRequest;
import pl.patrykbober.bloomer.user.request.RegisterUserRequest;
import pl.patrykbober.bloomer.user.request.SelfUpdateUserRequest;
import pl.patrykbober.bloomer.user.request.UpdateUserRequest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserService userService;

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void successfullyCreateUser() {
        // given
        var request = new CreateUserRequest("user@bloomer.com", "fn", "ln", "passwd", true, List.of("USER", "INVALID"));

        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(new Role(1L, "USER")));
        when(roleRepository.findByName("INVALID")).thenReturn(Optional.empty());

        // when
        userService.create(request);

        // then
        verify(userRepository).save(any(BloomerUser.class));
    }

    @Test
    void throwExceptionWhenCreateUserInvokedWithEmailAlreadyExists() {
        // given
        var request = new CreateUserRequest("user@bloomer.com", "fn", "ln", "passwd", true, List.of("USER"));

        when(userRepository.existsByEmail(any())).thenReturn(true);

        // when
        var thrown = catchThrowableOfType(() -> userService.create(request), BloomerException.class);

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS);
    }

    @Test
    void getAllUsersFromDatabase() {
        // given
        var user = BloomerUser.builder()
                .id(1L)
                .email("user@bloomer.com")
                .firstName("fn")
                .lastName("ln")
                .active(true)
                .password("encodedPassword")
                .roles(Set.of(Role.builder().id(1L).name("USER").build()))
                .build();

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDto(any(BloomerUser.class))).thenAnswer(mapInputToDto());

        // when
        var result = userService.findAll();

        // then
        assertThat(result)
                .isNotNull()
                .hasSize(1)
                .element(0)
                .returns(1L, UserDto::id)
                .returns("user@bloomer.com", UserDto::email)
                .returns("fn", UserDto::firstName)
                .returns("ln", UserDto::lastName)
                .returns(true, UserDto::active);
    }

    @Test
    void returnUserWithGivenIdIfFoundInDatabase() {
        // given
        var id = 1L;
        var user = BloomerUser.builder()
                .id(1L)
                .email("user@bloomer.com")
                .firstName("fn")
                .lastName("ln")
                .active(true)
                .password("encodedPassword")
                .roles(Set.of(Role.builder().id(1L).name("USER").build()))
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userMapper.toDto(any(BloomerUser.class))).thenAnswer(mapInputToDto());

        // when
        var result = userService.findById(id);

        // then
        assertThat(result)
                .isNotNull()
                .returns(1L, UserDto::id)
                .returns("user@bloomer.com", UserDto::email)
                .returns("fn", UserDto::firstName)
                .returns("ln", UserDto::lastName)
                .returns(true, UserDto::active);
    }

    @Test
    void throwExceptionWhenUserWithGivenIdNotFoundInDatabase() {
        // given
        var id = 1L;

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        var thrown = catchThrowableOfType(() -> userService.findById(id), BloomerException.class);

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void returnUserWithGivenEmailIfFoundInDatabase() {
        // given
        var email = "user@bloomer.com";
        var user = BloomerUser.builder()
                .id(1L)
                .email("user@bloomer.com")
                .firstName("fn")
                .lastName("ln")
                .active(true)
                .password("encodedPassword")
                .roles(Set.of(Role.builder().id(1L).name("USER").build()))
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userMapper.toDto(any(BloomerUser.class))).thenAnswer(mapInputToDto());

        // when
        var result = userService.findByEmail(email);

        // then
        assertThat(result)
                .isNotNull()
                .returns(1L, UserDto::id)
                .returns("user@bloomer.com", UserDto::email)
                .returns("fn", UserDto::firstName)
                .returns("ln", UserDto::lastName)
                .returns(true, UserDto::active);
    }

    @Test
    void throwExceptionWhenUserWithGivenEmailNotFoundInDatabase() {
        // given
        var email = "user@bloomer.com";

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // when
        var thrown = catchThrowableOfType(() -> userService.findByEmail(email), BloomerException.class);

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void successfullyUpdateUserByIdIfFoundInDatabase() {
        // given
        var id = 1L;
        var request = new UpdateUserRequest("newFn", "newLn", "newPassword", null, null);
        var user = BloomerUser.builder()
                .id(1L)
                .email("user@bloomer.com")
                .firstName("fn")
                .lastName("ln")
                .password("encodedPassword")
                .active(true)
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userMapper.toDto(any(BloomerUser.class))).thenAnswer(mapInputToDto());

        // when
        var result = userService.update(id, request);

        // then
        assertThat(result)
                .isNotNull()
                .returns(1L, UserDto::id)
                .returns("user@bloomer.com", UserDto::email)
                .returns("newFn", UserDto::firstName)
                .returns("newLn", UserDto::lastName)
                .returns(true, UserDto::active);
    }

    @Test
    void throwExceptionWhenUpdateInvokedForUserWithIdNotFoundInDatabase() {
        // given
        var id = 1L;
        var request = new UpdateUserRequest("newFn", "newLn", "newPassword", null, null);

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        var thrown = catchThrowableOfType(() -> userService.update(id, request), BloomerException.class);

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void successfullyUpdateUserByEmailIfFoundInDatabase() {
        // given
        var email = "user@bloomer.com";
        var request = new SelfUpdateUserRequest("newFn", "newLn");
        var user = BloomerUser.builder()
                .id(1L)
                .email("user@bloomer.com")
                .firstName("fn")
                .lastName("ln")
                .password("encodedPassword")
                .active(true)
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userMapper.toDto(any(BloomerUser.class))).thenAnswer(mapInputToDto());

        // when
        var result = userService.update(email, request);

        // then
        assertThat(result)
                .isNotNull()
                .returns(1L, UserDto::id)
                .returns("user@bloomer.com", UserDto::email)
                .returns("newFn", UserDto::firstName)
                .returns("newLn", UserDto::lastName)
                .returns(true, UserDto::active);
    }

    @Test
    void throwExceptionWhenUpdateInvokedForUserWithEmailNotFoundInDatabase() {
        // given
        var email = "user@bloomer.com";
        var request = new SelfUpdateUserRequest("newFn", "newLn");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // when
        var thrown = catchThrowableOfType(() -> userService.update(email, request), BloomerException.class);

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void successfullyDeleteUserByIdIfFoundInDatabase() {
        // given
        var id = 1L;

        // when
        userService.deleteById(id);

        // then
        verify(userRepository).deleteById(id);
    }

    @Test
    void throwExceptionWhenDeleteInvokedForUserWithIdNotFoundInDatabase() {
        // given
        var id = 1L;

        doThrow(new EmptyResultDataAccessException(1)).when(userRepository).deleteById(anyLong());

        // when
        var thrown = catchThrowableOfType(() -> userService.deleteById(id), BloomerException.class);

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void successfullyDeleteUserByEmailIfFoundInDatabase() {
        // given
        var email = "user@bloomer.com";

        // when
        userService.deleteByEmail(email);

        // then
        verify(userRepository).deleteByEmail(email);
    }

    @Test
    void throwExceptionWhenDeleteInvokedForUserWithEmailNotFoundInDatabase() {
        // given
        var email = "user@bloomer.com";

        doThrow(new EmptyResultDataAccessException(1)).when(userRepository).deleteByEmail(anyString());

        // when
        var thrown = catchThrowableOfType(() -> userService.deleteByEmail(email), BloomerException.class);

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void successfullyRegisterUser() {
        // given
        var request = new RegisterUserRequest("user@bloomer.com", "fn", "ln", "passwd");

        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

        // when
        userService.register(request);

        // then
        verify(userRepository).save(any(BloomerUser.class));
        verify(eventPublisher).publishEvent(any(OnUserRegistrationCompleteEvent.class));
    }

    @Test
    void throwExceptionWhenRegisterUserInvokedWithEmailAlreadyExists() {
        // given
        var request = new RegisterUserRequest("user@bloomer.com", "fn", "ln", "passwd");

        when(userRepository.existsByEmail(any())).thenReturn(true);

        // when
        var thrown = catchThrowableOfType(() -> userService.register(request), BloomerException.class);

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS);
    }

    private Answer<UserDto> mapInputToDto() {
        return i -> mapper.toDto(i.getArgument(0, BloomerUser.class));
    }

}
