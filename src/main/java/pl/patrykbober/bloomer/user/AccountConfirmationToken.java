package pl.patrykbober.bloomer.user;

import lombok.*;
import pl.patrykbober.bloomer.common.jpa.AbstractBaseEntity;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account_confirmation_token")
public class AccountConfirmationToken extends AbstractBaseEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_confirmation_token_id_seq")
    @SequenceGenerator(name = "account_confirmation_token_id_seq", sequenceName = "account_confirmation_token_id_seq", allocationSize = 1)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private BloomerUser user;

    @Column(name = "token", nullable = false, updatable = false, unique = true)
    private String token;

    @Column(name = "expiration_date", nullable = false, updatable = false)
    private ZonedDateTime expirationDate;

    @Setter
    @Column(name = "used", nullable = false)
    private boolean used = false;

}
