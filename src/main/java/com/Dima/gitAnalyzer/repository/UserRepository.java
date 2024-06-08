package com.Dima.gitAnalyzer.repository;

import com.Dima.gitAnalyzer.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT u.* FROM users u WHERE u.username = ?1", nativeQuery = true)
    User findByUsername(String username);
    @Query(value = "SELECT u.password FROM users u WHERE u.username = ?1", nativeQuery = true)
    String findPasswordByUsername(String username);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO authorities VALUES (?1, 'USER')", nativeQuery = true)
    void addAuthority(String username);
}

