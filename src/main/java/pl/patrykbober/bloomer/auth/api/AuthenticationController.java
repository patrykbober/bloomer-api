package pl.patrykbober.bloomer.auth.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/auth")
@RequiredArgsConstructor
class AuthenticationController {

    private final AuthenticationApiService authenticationApiService;

    @PostMapping("login")
    ResponseEntity<AuthResponseDto> login(@RequestBody CredentialsAuthRequestDto credentialsAuthRequest) {
        var response = authenticationApiService.login(credentialsAuthRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("refresh")
    ResponseEntity<AuthResponseDto> refresh(@RequestBody RefreshTokenAuthRequestDto refreshTokenAuthRequest) {
        var response = authenticationApiService.refresh(refreshTokenAuthRequest);
        return ResponseEntity.ok(response);
    }

}
