package com.Dima.gitAnalyzer.repository;

import com.Dima.gitAnalyzer.entity.ChangeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangeRepository extends JpaRepository<ChangeEntity, Long> {
}