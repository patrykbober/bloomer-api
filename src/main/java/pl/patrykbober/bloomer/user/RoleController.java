package pl.patrykbober.bloomer.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.patrykbober.bloomer.user.request.UserRolesRequest;
import pl.patrykbober.bloomer.user.response.RolesResponse;

@RestController
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/roles")
    public ResponseEntity<RolesResponse> getAll() {
        var roles = roleService.findAll();
        var response = new RolesResponse(roles);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{userId}/roles")
    public ResponseEntity<Void> addRolesToUser(@PathVariable Long userId, @RequestBody UserRolesRequest request) {
        roleService.addRolesToUser(userId, request.roleNames());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{userId}/roles")
    public ResponseEntity<Void> deleteRolesFromUser(@PathVariable Long userId, @RequestBody UserRolesRequest request) {
        roleService.deleteRolesFromUser(userId, request.roleNames());
        return ResponseEntity.noContent().build();
    }


}
