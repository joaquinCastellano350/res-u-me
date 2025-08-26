package com.joaquin.backend.resume.domain;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "tags", uniqueConstraints = @UniqueConstraint(name = "uk_tags_name", columnNames = "name"))
public class Tag {
    @Id
    private UUID id;

    @Column(name = "name", nullable = false, columnDefinition = "citext")
    private String name;

    @PrePersist
    public void prePersist() {
        if (id == null) id =  UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
