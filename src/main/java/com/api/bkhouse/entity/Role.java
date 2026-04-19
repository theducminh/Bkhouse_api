package com.api.bkhouse.entity;

import javax.persistence.*;
import com.api.bkhouse.constant.enumeric.ERole;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "roles") // Đảm bảo bảng trong Postgres là "roles"
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", length = 50, nullable = false, unique = true)
    private ERole name;

    // 1. THÊM: Cột description theo mô tả bảng
    @Column(name = "description", columnDefinition = "text")
    private String description;

    // 2. THÊM: Cột created_at theo mô tả bảng
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    public Role() {}

    public Role(ERole name) {
        this.name = name;
    }

    // --- GETTERS & SETTERS ---

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public ERole getName() { return name; }
    public void setName(ERole name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        Role role = (Role) o;
        return name == role.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}