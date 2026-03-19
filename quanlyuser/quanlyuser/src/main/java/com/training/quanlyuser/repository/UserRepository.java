package com.training.quanlyuser.repository;

import com.training.quanlyuser.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;


//hà hồng dương -12/3/2026
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByResetToken(String token);

}