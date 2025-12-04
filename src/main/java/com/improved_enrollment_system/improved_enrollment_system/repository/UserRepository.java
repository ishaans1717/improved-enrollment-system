package com.improved_enrollment_system.improved_enrollment_system.repository;

import com.improved_enrollment_system.improved_enrollment_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByEmail(String email);
    //Optional<User> findByUsername(String username);
}