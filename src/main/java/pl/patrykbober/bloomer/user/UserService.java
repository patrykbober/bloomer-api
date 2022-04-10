package pl.patrykbober.bloomer.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.patrykbober.bloomer.common.exception.BloomerException;
import pl.patrykbober.bloomer.common.exception.ErrorCode;
import pl.patrykbober.bloomer.user.dto.UserDto;
import pl.patrykbober.bloomer.user.event.OnUserRegistrationCompleteEvent;
import pl.patrykbober.bloomer.user.request.CreateUserRequest;
import pl.patrykbober.bloomer.user.request.RegisterUserRequest;
import pl.patrykbober.bloomer.user.request.SelfUpdateUserRequest;
import pl.patrykbober.bloomer.user.request.UpdateUserRequest;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    public Long create(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BloomerException(ErrorCode.EMAIL_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }

        var roles = roleRepository.findByNameIn(request.roles().stream()
                .map(String::trim)
                .map(String::toUpperCase)
                .toList());
        var user = BloomerUser.builder()
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .password(passwordEncoder.encode(request.password()))
                .active(request.active())
                .roles(new HashSet<>(roles))
                .build();

        userRepository.save(user);
        return user.getId();
    }

    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserDto findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new BloomerException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public UserDto findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> new BloomerException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public UserDto update(Long id, UpdateUserRequest request) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new BloomerException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        user.setIfNotNull(user::setFirstName, request.firstName());
        user.setIfNotNull(user::setLastName, request.lastName());
        user.setIfNotNull(user::setPassword, passwordEncoder.encode(request.password()));
        user.setIfNotNull(user::setActive, request.active());

        userRepository.save(user);
        return userMapper.toDto(user);
    }

    public UserDto update(String email, SelfUpdateUserRequest request) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BloomerException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        user.setIfNotNull(user::setFirstName, request.firstName());
        user.setIfNotNull(user::setLastName, request.lastName());

        userRepository.save(user);
        return userMapper.toDto(user);
    }

    public void deleteById(Long id) {
        try {
            userRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new BloomerException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
    }

    public void deleteByEmail(String email) {
        try {
            userRepository.deleteByEmail(email);
        } catch (EmptyResultDataAccessException e) {
            throw new BloomerException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
    }

    public Long register(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BloomerException(ErrorCode.EMAIL_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }

        var defaultRoles = roleRepository.findDefaultRoles();
        var user = BloomerUser.builder()
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .password(passwordEncoder.encode(request.password()))
                .active(false)
                .roles(new HashSet<>(defaultRoles))
                .build();

        userRepository.save(user);
        eventPublisher.publishEvent(new OnUserRegistrationCompleteEvent(user.getId()));

        return user.getId();
    }
}
