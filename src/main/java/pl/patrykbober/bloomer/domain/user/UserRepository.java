package pl.patrykbober.bloomer.domain.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<BloomerUser, Long> {

    @EntityGraph(attributePaths = {"roles"})
    Optional<BloomerUser> findByEmail(String email);

}
