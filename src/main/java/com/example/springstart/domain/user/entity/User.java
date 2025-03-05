package com.example.springstart.domain.user.entity;

import com.example.springstart.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class User extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String username;

    @Column(nullable = false)
    String password;

    @Enumerated(EnumType.STRING)
    private UserRoleType role;

    @Column(nullable = false)
    String email;

    @Column(nullable = false)
    Boolean Banned = false;

    Integer passwordCount;

    @Builder
    public User(String username, String password, UserRoleType role, String email) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
    }

    @PrePersist
    public void prePersist() {
        passwordCount = 0;

        if (Banned == null) {
            Banned = false;
        }
    }

    public void updateUser(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateBan(boolean Banned) {
        this.Banned = Banned;
    }
}