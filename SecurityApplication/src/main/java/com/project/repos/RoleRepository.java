package com.project.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
