package com.example.springstart.domain.user.repository;

import com.example.springstart.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository
        extends JpaRepository<User, Long>, UserRepositoryCustom {

    Optional<User> findByUsername(String username);
    //Optional<User> findByUserId(String userId);

    Boolean existsByUsername(String username);
}