package pl.patrykbober.bloomer;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final TokenService tokenService;

    @PreAuthorize("hasAuthority('SCOPE_admin')")
    @GetMapping("/")
    public String hello(Authentication authentication) {
        return "Hello, " + authentication.getName() + "!";
    }

    @PostMapping("/token")
    public ResponseEntity<AccessTokenResponse> token(Authentication authentication) {
        var response = tokenService.getAccessTokenResponse(authentication);
        return ResponseEntity.ok(response);
    }

}
