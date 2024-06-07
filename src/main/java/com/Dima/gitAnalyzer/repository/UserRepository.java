package com.Dima.gitAnalyzer.repository;

import com.Dima.gitAnalyzer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    @Query(value = "SELECT u.password FROM users u WHERE u.username = ?1", nativeQuery = true)
    String findPasswordByUsername(String username);
}

