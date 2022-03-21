package pl.patrykbober.bloomer.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @PostMapping("/token")
    public ResponseEntity<AccessTokenResponse> token(Authentication authentication) {
        var response = tokenService.getAccessTokenResponse(authentication);
        return ResponseEntity.ok(response);
    }

}
