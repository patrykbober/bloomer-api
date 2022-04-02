package pl.patrykbober.bloomer.common.jpa;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.function.Consumer;

@Getter
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
public abstract class AbstractBaseEntity implements Serializable {

    @CreationTimestamp
    @Column(name = "creation_date", nullable = false, updatable = false)
    protected ZonedDateTime creationDate;

    @UpdateTimestamp
    @Column(name = "update_date", nullable = false)
    protected ZonedDateTime updateDate;

    @Version
    @Column(name = "optlock_version")
    protected Long version;

    public <T> void setIfNotNull(final Consumer<T> setter, final T value) {
        if (value != null) {
            setter.accept(value);
        }
    }

}
