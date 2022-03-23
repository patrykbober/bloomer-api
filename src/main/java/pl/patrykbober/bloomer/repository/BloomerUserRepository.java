package pl.patrykbober.bloomer.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.patrykbober.bloomer.model.jpa.BloomerUser;

import java.util.Optional;

@Repository
public interface BloomerUserRepository extends JpaRepository<BloomerUser, Long> {

    @EntityGraph(attributePaths = {"roles"})
    Optional<BloomerUser> findByEmail(String email);

}
