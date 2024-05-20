package com.archworker.coreapplication.repository;

import com.archworker.coreapplication.entity.Role;
import com.archworker.coreapplication.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRole(RoleEnum name);
}
