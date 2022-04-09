package pl.patrykbober.bloomer.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountConfirmationTokenRepository extends JpaRepository<AccountConfirmationToken, Long> {

    @EntityGraph(attributePaths = {"user"})
    Optional<AccountConfirmationToken> findByToken(String token);

}
