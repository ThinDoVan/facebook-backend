package com.example.facebookbackend.repositories;

import com.example.facebookbackend.entities.Role;
import com.example.facebookbackend.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRole(ERole roleName);
}
