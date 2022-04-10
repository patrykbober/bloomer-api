package pl.patrykbober.bloomer.mail;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private MailService mailService;

    @Test
    void sendAccountConfirmationEmail() {
        // given
        var recipient = "user@bloomer.com";
        var confirmationUrl = "url";

        // when
        mailService.sendAccountConfirmationEmail(List.of(recipient), confirmationUrl);

        // then
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

}