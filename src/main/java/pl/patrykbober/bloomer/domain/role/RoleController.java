package pl.patrykbober.bloomer.domain.role;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.patrykbober.bloomer.domain.role.response.RolesResponse;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<RolesResponse> getAll() {
        var roles = roleService.findAll();
        var response = new RolesResponse(roles);
        return ResponseEntity.ok(response);
    }

}
