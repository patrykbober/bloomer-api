package pl.patrykbober.bloomer.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<BloomerUser, Long> {

    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = {"roles"})
    Optional<BloomerUser> findByEmail(String email);

    void deleteByEmail(String email);

}
