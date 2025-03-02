package com.example.springstart.domain.user.entity;

import com.example.springstart.domain.common.BaseEntity;
import com.example.springstart.domain.user.dto.UserUpdateRequestDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String username;
    String password;

    @Enumerated(EnumType.STRING)
    private UserRoleType role;

    @Builder
    public User(String username, String password, UserRoleType role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public void updateUser(String username, String password) {
        this.username = username;
        this.password = password;
    }
}