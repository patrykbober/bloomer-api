package pl.patrykbober.bloomer.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findByNameIn(Collection<String> names);

    @Query("SELECT r FROM Role r WHERE r.defaultRole = TRUE")
    List<Role> findDefaultRoles();
}
