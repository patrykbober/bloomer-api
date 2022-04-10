package pl.patrykbober.bloomer.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import pl.patrykbober.bloomer.common.exception.BloomerException;
import pl.patrykbober.bloomer.common.exception.ErrorCode;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountConfirmationTokenServiceTest {

    @Value("${app.account_confirmation.token.expiry}")
    private long tokenExpiry;

    @Mock
    private AccountConfirmationTokenRepository accountConfirmationTokenRepository;

    @InjectMocks
    private AccountConfirmationTokenService accountConfirmationTokenService;

    @Test
    void successfullyCreateToken() {
        // given
        var user = BloomerUser.builder()
                .id(1L)
                .build();

        // when
        var actual = accountConfirmationTokenService.create(user);

        // then
        verify(accountConfirmationTokenRepository).save(any());
        assertThat(actual).isNotNull();
        assertThat(actual.getUser().getId()).isEqualTo(user.getId());
        assertThat(actual.getToken()).isNotNull();
        assertThat(actual.getExpirationDate()).isCloseTo(ZonedDateTime.now().plusSeconds(tokenExpiry), within(3, ChronoUnit.SECONDS));
        assertThat(actual.isUsed()).isFalse();
    }

    @Test
    void successfullyConfirmAccount() {
        // given
        var token = "valid-token";
        var user = BloomerUser.builder()
                .id(1L)
                .email("user@bloomer.com")
                .active(false)
                .build();
        var confirmationToken = AccountConfirmationToken.builder()
                .id(1L)
                .user(user)
                .token(token)
                .expirationDate(ZonedDateTime.now().plusHours(20))
                .used(false)
                .build();

        when(accountConfirmationTokenRepository.findByToken(any())).thenReturn(Optional.of(confirmationToken));

        // when
        accountConfirmationTokenService.confirm(token);

        // then
        assertThat(user.isActive()).isTrue();
        assertThat(confirmationToken.isUsed()).isTrue();
    }

    @Test
    void throwExceptionWhenConfirmationTokenNotFound() {
        // given
        var token = "invalid-token";

        when(accountConfirmationTokenRepository.findByToken(any())).thenReturn(Optional.empty());

        // when
        var thrown = catchThrowableOfType(() -> accountConfirmationTokenService.confirm(token), BloomerException.class);

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.INVALID_ACCOUNT_CONFIRMATION_TOKEN);
    }

    @Test
    void throwExceptionWhenTokenHasAlreadyBeenUsed() {
        // given
        var token = "used-token";
        var confirmationToken = AccountConfirmationToken.builder()
                .id(1L)
                .token(token)
                .expirationDate(ZonedDateTime.now().plusHours(20))
                .used(true)
                .build();

        when(accountConfirmationTokenRepository.findByToken(any())).thenReturn(Optional.of(confirmationToken));

        // when
        var thrown = catchThrowableOfType(() -> accountConfirmationTokenService.confirm(token), BloomerException.class);

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.ACCOUNT_CONFIRMATION_TOKEN_ALREADY_USED);
    }

    @Test
    void throwExceptionWhenTokenExpired() {
        // given
        var token = "expired-token";
        var confirmationToken = AccountConfirmationToken.builder()
                .id(1L)
                .token(token)
                .expirationDate(ZonedDateTime.now().minusHours(2))
                .used(false)
                .build();

        when(accountConfirmationTokenRepository.findByToken(any())).thenReturn(Optional.of(confirmationToken));

        // when
        var thrown = catchThrowableOfType(() -> accountConfirmationTokenService.confirm(token), BloomerException.class);

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.ACCOUNT_CONFIRMATION_TOKEN_EXPIRED);
    }

    @Test
    void throwAssertionErrorWhenUserNotFound() {
        // given
        var token = "token-with-no-user";
        var confirmationToken = AccountConfirmationToken.builder()
                .id(1L)
                .token(token)
                .expirationDate(ZonedDateTime.now().plusHours(20))
                .used(false)
                .build();

        when(accountConfirmationTokenRepository.findByToken(any())).thenReturn(Optional.of(confirmationToken));

        // when
        var thrown = catchThrowableOfType(() -> accountConfirmationTokenService.confirm(token), AssertionError.class);

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown.getMessage()).isEqualTo("User was not found for token token-with-no-user");

    }

}