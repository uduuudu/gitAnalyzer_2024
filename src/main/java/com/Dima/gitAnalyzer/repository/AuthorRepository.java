package com.Dima.gitAnalyzer.repository;

import com.Dima.gitAnalyzer.entity.AuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<AuthorEntity, Long> {

    @Query(value = "SELECT authors.* FROM authors JOIN commits ON commits.author_id = authors.id WHERE authors.name = ?1 AND commits.project_id = ?2", nativeQuery = true)
    Optional<AuthorEntity> findByNameAndProjectId(String name, Long projectId);

    @Query(value = "SELECT authors.* FROM authors JOIN commits ON commits.author_id = authors.id WHERE commits.project_id = ?1", nativeQuery = true)
    List<AuthorEntity> findByRepository(Long repoId);



}
