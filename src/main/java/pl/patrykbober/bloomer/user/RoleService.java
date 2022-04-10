package pl.patrykbober.bloomer.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.patrykbober.bloomer.common.exception.BloomerException;
import pl.patrykbober.bloomer.common.exception.ErrorCode;
import pl.patrykbober.bloomer.user.dto.RoleDto;

import java.util.List;

@Slf4j
@Transactional(readOnly = true)
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

    @Transactional
    public void addRolesToUser(Long userId, List<String> roleNames) {
        var user = getUserByIdOrThrow(userId);
        getRolesByNameIn(roleNames).forEach(user::addRole);
    }

    @Transactional
    public void deleteRolesFromUser(Long userId, List<String> roleNames) {
        var user = getUserByIdOrThrow(userId);
        getRolesByNameIn(roleNames).forEach(user::removeRole);
    }

    private BloomerUser getUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with id {} was not found", userId);
                    return new BloomerException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
                });
    }

    private List<Role> getRolesByNameIn(List<String> roleNames) {
        return roleRepository.findByNameIn(roleNames.stream()
                .map(String::trim)
                .map(String::toUpperCase)
                .toList());
    }
}
