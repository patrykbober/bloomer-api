package pl.patrykbober.bloomer.auth;

import pl.patrykbober.bloomer.auth.model.AuthResponse;
import pl.patrykbober.bloomer.auth.model.CredentialsAuthRequest;
import pl.patrykbober.bloomer.auth.model.RefreshTokenAuthRequest;

public interface AuthenticationFacade {

    AuthResponse login(CredentialsAuthRequest credentialsAuthRequest);

    AuthResponse refresh(RefreshTokenAuthRequest refreshTokenAuthRequest);

}
