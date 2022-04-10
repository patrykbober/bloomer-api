package pl.patrykbober.bloomer.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

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

        mailSender.send(message);
    }

}
