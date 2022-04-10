package pl.patrykbober.bloomer.mail;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
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

    @Test
    void logErrorWhenMailExceptionIsThrown(CapturedOutput output) {
        // given
        var recipient = "user@bloomer.com";
        var confirmationUrl = "url";

        doThrow(new MailSendException("error")).when(mailSender).send(any(SimpleMailMessage.class));

        // when
        var thrown = catchThrowableOfType(() -> mailService.sendAccountConfirmationEmail(List.of(recipient), confirmationUrl), MailException.class);

        // then
        assertThat(output.getOut()).contains("Error occurred while sending email to user@bloomer.com");
        assertThat(thrown).isNotNull();
        assertThat(thrown).isInstanceOf(MailSendException.class);
    }

}