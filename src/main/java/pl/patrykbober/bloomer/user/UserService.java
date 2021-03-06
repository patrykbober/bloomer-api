package pl.patrykbober.bloomer.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Long create(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            log.error("User with email {} already exists", request.email());
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
        log.info("User with email {} has been saved with id {}", user.getEmail(), user.getId());
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
                .orElseThrow(() -> {
                    log.error("User with id {} was not found", id);
                    return new BloomerException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
                });
    }

    public UserDto findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> {
                    log.error("User with email {} was not found", email);
                    return new BloomerException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
                });
    }

    @Transactional
    public UserDto update(Long id, UpdateUserRequest request) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User with id {} was not found", id);
                    return new BloomerException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
                });

        user.setIfNotNull(user::setFirstName, request.firstName());
        user.setIfNotNull(user::setLastName, request.lastName());
        user.setIfNotNull(user::setPassword, request.password() != null ? passwordEncoder.encode(request.password()) : null);
        user.setIfNotNull(user::setActive, request.active());

        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto update(String email, SelfUpdateUserRequest request) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User with email {} was not found", email);
                    return new BloomerException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
                });

        user.setIfNotNull(user::setFirstName, request.firstName());
        user.setIfNotNull(user::setLastName, request.lastName());

        return userMapper.toDto(user);
    }

    @Transactional
    public void deleteById(Long id) {
        try {
            userRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Error occurred while deleting user with id {}", id, e);
            throw new BloomerException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    public void deleteByEmail(String email) {
        try {
            userRepository.deleteByEmail(email);
        } catch (EmptyResultDataAccessException e) {
            log.error("Error occurred while deleting user with email {}", email, e);
            throw new BloomerException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    public Long register(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            log.error("User with email {} already exists", request.email());
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
        log.info("User with email {} has been registered with id {}", user.getEmail(), user.getId());
        eventPublisher.publishEvent(new OnUserRegistrationCompleteEvent(user.getId()));

        return user.getId();
    }
}
