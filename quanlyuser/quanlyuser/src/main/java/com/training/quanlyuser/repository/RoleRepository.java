package com.training.quanlyuser.repository;

import com.training.quanlyuser.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//hà hồng dương -18/3/2026
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);
}
