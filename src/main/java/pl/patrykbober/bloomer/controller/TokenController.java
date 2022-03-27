package pl.patrykbober.bloomer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.patrykbober.bloomer.model.response.AccessTokenResponse;
import pl.patrykbober.bloomer.service.TokenService;

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
