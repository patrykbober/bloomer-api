package pl.patrykbober.bloomer.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.patrykbober.bloomer.common.exception.BloomerException;
import pl.patrykbober.bloomer.common.exception.ErrorCode;

import java.time.ZonedDateTime;
import java.util.UUID;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class AccountConfirmationTokenService {

    @Value("${app.account_confirmation.token.expiry}")
    private long tokenExpiry;

    private final AccountConfirmationTokenRepository accountConfirmationTokenRepository;

    public AccountConfirmationToken create(BloomerUser user) {
        var token = AccountConfirmationToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expirationDate(ZonedDateTime.now().plusSeconds(tokenExpiry))
                .build();

        accountConfirmationTokenRepository.save(token);
        log.info("Account confirmation token for user with id {} has been saved with value {}", user.getId(), token.getToken());
        return token;
    }

    public void confirm(String token) {
        var confirmationToken = accountConfirmationTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.error("Account confirmation token {} is invalid", token);
                    return new BloomerException(ErrorCode.INVALID_ACCOUNT_CONFIRMATION_TOKEN, HttpStatus.BAD_REQUEST);
                });

        if (confirmationToken.isUsed()) {
            log.error("Account confirmation token {} has already been used", token);
            throw new BloomerException(ErrorCode.ACCOUNT_CONFIRMATION_TOKEN_ALREADY_USED, HttpStatus.BAD_REQUEST);
        }

        if (ZonedDateTime.now().isAfter(confirmationToken.getExpirationDate())) {
            log.error("Account confirmation token {} expired", token);
            throw new BloomerException(ErrorCode.ACCOUNT_CONFIRMATION_TOKEN_EXPIRED, HttpStatus.BAD_REQUEST);
        }

        var user = confirmationToken.getUser();
        if (user == null) {
            log.error("No user is assigned to confirmation token {}", token);
            throw new AssertionError(String.format("User was not found for token %s", token));
        }

        user.setActive(true);
        confirmationToken.setUsed(true);
        log.info("User with id {} has been confirmed and enabled", user.getId());
    }
}
