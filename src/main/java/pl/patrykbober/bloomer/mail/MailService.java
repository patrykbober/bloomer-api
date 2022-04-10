package pl.patrykbober.bloomer.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendAccountConfirmationEmail(List<String> recipients, String confirmationUrl) {
        var message = new SimpleMailMessage();
        message.setFrom("noreply@bloomer.com");
        message.setTo(recipients.toArray(new String[0]));
        message.setSubject("Bloomer: confirm your account");
        message.setText(String.format("Click the link in order to verify your account:%n%s", confirmationUrl));

        log.info("Sending account confirmation email to {}", String.join(", ", recipients));
        try {
            mailSender.send(message);
            log.info("Account confirmation email has been sent: {}", message);
        } catch (MailException e) {
            log.error("Error occurred while sending email to {}", String.join(", ", recipients), e);
            throw e;
        }
    }

}
