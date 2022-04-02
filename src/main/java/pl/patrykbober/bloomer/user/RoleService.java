package pl.patrykbober.bloomer.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.patrykbober.bloomer.user.dto.RoleDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public List<RoleDto> findAll() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toDto)
                .collect(Collectors.toList());
    }

}
