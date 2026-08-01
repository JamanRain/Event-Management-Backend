
package com.campus.EventManagement.Repositories;
import com.campus.EventManagement.Entities.Role;
import com.campus.EventManagement.Entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Existing
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<User> findByRole(Role role, Pageable pageable);

    @Query("SELECT u.name FROM User u WHERE u.role = :role")
    Page<String> findNamesByRole(
            @Param("role") Role role,
            Pageable pageable
    );

    void deleteByEmail(String email);

    long countByRole(Role role);

    long count();

    Page<User> findByNameContainingIgnoreCase(
            String name,
            Pageable pageable
    );

    Page<User> findByEmailContainingIgnoreCase(
            String email,
            Pageable pageable
    );
    List<User> findAllByRole(Role role);

    boolean existsByRole(Role role);

    List<User> findByIdIn(List<Long> ids);

    List<User> findTop10ByOrderByCreatedAtDesc();

    @Query("""
            SELECT COUNT(r)
            FROM Registration r
            WHERE r.user.id = :userId
           """)
    long countRegistrationsByUser(
            @Param("userId") Long userId
    );
    @Query("""
            SELECT COUNT(e)
            FROM Event e
            WHERE e.createdBy.id = :userId
           """)
    long countEventsCreatedByUser(
            @Param("userId") Long userId
    );
}