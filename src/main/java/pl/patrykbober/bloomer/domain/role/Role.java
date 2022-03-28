package pl.patrykbober.bloomer.domain.role;

import lombok.Getter;
import lombok.Setter;
import pl.patrykbober.bloomer.common.jpa.AbstractBaseEntity;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "role")
public class Role extends AbstractBaseEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_id_seq")
    @SequenceGenerator(name = "role_id_seq", sequenceName = "role_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

}
