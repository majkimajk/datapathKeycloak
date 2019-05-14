package org.keycloak.datapath.model;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * @author mbaran@bluesoft.net.pl
 */
@MappedSuperclass
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    protected Long id;

    @Column(name = "uuid", nullable = false, unique = true, updatable = false)
    protected String uuid = UUID.randomUUID().toString();

    @Column(name = "CREATED_TIME", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createdTime;

    @PrePersist
    public void prePersist() {
        this.createdTime = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractEntity that = (AbstractEntity) o;
        return Objects.equals(uuid, that.uuid);
    }
}
