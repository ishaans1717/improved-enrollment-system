package com.improved_enrollment_system.improved_enrollment_system.repository;

import com.improved_enrollment_system.improved_enrollment_system.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    Optional<UserSession> findBySessionToken(String sessionToken);
    List<UserSession> findByUserIdAndIsActive(Long userId, Boolean isActive);
    List<UserSession> findByUserId(Long userId);
    List<UserSession> findByUserIdAndIsActiveTrue(Long userId);
}