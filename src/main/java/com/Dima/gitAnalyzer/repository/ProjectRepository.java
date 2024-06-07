package com.Dima.gitAnalyzer.repository;

import com.Dima.gitAnalyzer.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    Optional<ProjectEntity> findByUrl(String repositoryUrl);
}