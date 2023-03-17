package pl.patrykbober.bloomer.auth.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.patrykbober.bloomer.auth.AuthenticationFacade;
import pl.patrykbober.bloomer.auth.exception.AuthenticationFailedException;

@Service
@RequiredArgsConstructor
class AuthenticationApiService {

    private final AuthenticationFacade authenticationFacade;

    AuthResponseDto login(CredentialsAuthRequestDto credentialsAuthRequest) {
        try {
            var authResponse = authenticationFacade.login(credentialsAuthRequest.toAuthRequest());
            return AuthResponseDto.ofAuthResponse(authResponse);
        } catch (AuthenticationFailedException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage(), ex);
        }
    }

    AuthResponseDto refresh(RefreshTokenAuthRequestDto refreshTokenAuthRequest) {
        try {
            var authResponse = authenticationFacade.refresh(refreshTokenAuthRequest.toAuthRequest());
            return AuthResponseDto.ofAuthResponse(authResponse);
        } catch (AuthenticationFailedException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage(), ex);
        }
    }
}
