package pl.patrykbober.bloomer.model.jpa;

import lombok.Getter;
import lombok.Setter;
import pl.patrykbober.bloomer.commons.jpa.AbstractBaseEntity;

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

    @Column(name = "name")
    private String name;

}
