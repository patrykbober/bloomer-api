package pl.patrykbober.bloomer.user.listener;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.StringEndsWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pl.patrykbober.bloomer.mail.MailService;
import pl.patrykbober.bloomer.user.AccountConfirmationToken;
import pl.patrykbober.bloomer.user.AccountConfirmationTokenService;
import pl.patrykbober.bloomer.user.BloomerUser;
import pl.patrykbober.bloomer.user.UserRepository;
import pl.patrykbober.bloomer.user.event.OnUserRegistrationCompleteEvent;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

@ExtendWith(MockitoExtension.class)
class OnUserRegistrationCompleteEventListenerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MailService mailService;

    @Mock
    private AccountConfirmationTokenService accountConfirmationTokenService;

    @InjectMocks
    private OnUserRegistrationCompleteEventListener listener;

    @BeforeEach
    void init() {
        var request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void successfullyHandleEvent() {
        // given
        var user = BloomerUser.builder()
                .id(1L)
                .email("user@bloomer.com")
                .build();
        var token = AccountConfirmationToken.builder()
                .token("valid_token")
                .build();
        var event = new OnUserRegistrationCompleteEvent(1L);

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(accountConfirmationTokenService.create(any())).thenReturn(token);

        // when
        listener.handleEvent(event);

        // then
        verify(mailService).sendAccountConfirmationEmail(argThat(recipientListContains("user@bloomer.com")), argThat(new StringEndsWith("?token=valid_token")));
    }

    @Test
    void throwAssertionErrorWhenUserNotFoundInDatabase() {
        // given
        var event = new OnUserRegistrationCompleteEvent(1L);

        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // when
        var thrown = catchThrowableOfType(() -> listener.handleEvent(event), AssertionError.class);

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown.getMessage()).isEqualTo("User with id 1 was not found");
    }

    private Matcher<List<String>> recipientListContains(String email) {
        return new TypeSafeMatcher<>() {
            @Override
            protected boolean matchesSafely(List<String> strings) {
                return strings.contains(email);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a list containing " + email);
            }
        };
    }

}