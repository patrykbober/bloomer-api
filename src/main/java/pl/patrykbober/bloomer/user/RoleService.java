package pl.patrykbober.bloomer.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.patrykbober.bloomer.common.exception.BloomerException;
import pl.patrykbober.bloomer.common.exception.ErrorCode;
import pl.patrykbober.bloomer.user.dto.RoleDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public List<RoleDto> findAll() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toDto)
                .toList();
    }

    public void addRolesToUser(Long userId, List<String> roleNames) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new BloomerException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        roleRepository.findByNameIn(roleNames.stream()
                        .map(String::trim)
                        .map(String::toUpperCase)
                        .toList())
                .forEach(user::addRole);

        userRepository.save(user);
    }

    public void deleteRolesFromUser(Long userId, List<String> roleNames) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new BloomerException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        roleRepository.findByNameIn(roleNames.stream()
                        .map(String::trim)
                        .map(String::toUpperCase)
                        .toList())
                .forEach(user::removeRole);

        userRepository.save(user);
    }
}
