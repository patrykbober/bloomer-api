package pl.patrykbober.bloomer.user.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import pl.patrykbober.bloomer.common.util.UriUtil;
import pl.patrykbober.bloomer.mail.MailService;
import pl.patrykbober.bloomer.user.AccountConfirmationTokenService;
import pl.patrykbober.bloomer.user.UserRepository;
import pl.patrykbober.bloomer.user.event.OnUserRegistrationCompleteEvent;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OnUserRegistrationCompleteEventListener {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final AccountConfirmationTokenService accountConfirmationTokenService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void handleEvent(OnUserRegistrationCompleteEvent event) {
        log.info("Handling received event: {}", event);
        var user = userRepository.findById(event.userId())
                .orElseThrow(() -> {
                    log.error("User with id {} was not found", event.userId());
                    return new AssertionError(String.format("User with id %s was not found", event.userId()));
                });

        var token = accountConfirmationTokenService.create(user);
        var confirmationUrl = UriUtil.requestConfirmationLink(token.getToken());

        mailService.sendAccountConfirmationEmail(List.of(user.getEmail()), confirmationUrl.toString());
    }

}
