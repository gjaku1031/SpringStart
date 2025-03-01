package com.example.springstart.domain.user.entity;

import com.example.springstart.domain.common.entiry.BaseEntity;
import jakarta.persistence.*;
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

}