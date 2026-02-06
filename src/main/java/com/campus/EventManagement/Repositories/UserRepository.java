package com.campus.EventManagement.Repositories;

import com.campus.EventManagement.Entities.Role;
import com.campus.EventManagement.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<User> findByRole(Role role, Pageable pageable);

    @Query("select u.name from User u where u.role = :role")
    Page<String> findNamesByRole(@Param("role") Role role, Pageable pageable);

    void deleteByEmail(String email);
}

