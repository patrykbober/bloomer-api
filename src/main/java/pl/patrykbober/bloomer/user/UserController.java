package pl.patrykbober.bloomer.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.patrykbober.bloomer.common.util.UriUtil;
import pl.patrykbober.bloomer.user.dto.UserDto;
import pl.patrykbober.bloomer.user.request.CreateUserRequest;
import pl.patrykbober.bloomer.user.request.RegisterUserRequest;
import pl.patrykbober.bloomer.user.request.SelfUpdateUserRequest;
import pl.patrykbober.bloomer.user.request.UpdateUserRequest;
import pl.patrykbober.bloomer.user.response.UsersResponse;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AccountConfirmationTokenService accountConfirmationTokenService;

    @GetMapping
    public ResponseEntity<UsersResponse> getAll() {
        var users = userService.findAll();
        var response = new UsersResponse(users);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {
        var user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody CreateUserRequest request) {
        var id = userService.create(request);
        var location = UriUtil.requestUriWithId(id);
        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        var user = userService.update(id, request);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterUserRequest request) {
        var id = userService.register(request);
        var location = UriUtil.requestUriWithPathAndId("/users", id);
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/confirm")
    public ResponseEntity<Void> confirmAccount(@RequestParam("token") String token) {
        accountConfirmationTokenService.confirm(token);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getLoggedInUser(Authentication auth) {
        var username = auth.getName();
        var user = userService.findByEmail(username);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/me")
    public ResponseEntity<UserDto> updateLoggedInUser(Authentication auth, @RequestBody SelfUpdateUserRequest request) {
        var username = auth.getName();
        var user = userService.update(username, request);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteLoggedInUser(Authentication auth) {
        var username = auth.getName();
        userService.deleteByEmail(username);
        return ResponseEntity.noContent().build();
    }

}
