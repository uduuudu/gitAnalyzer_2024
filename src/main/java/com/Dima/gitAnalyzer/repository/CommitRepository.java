package com.Dima.gitAnalyzer.repository;

import com.Dima.gitAnalyzer.entity.CommitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CommitRepository extends JpaRepository<CommitEntity, Long> {


    @Query(value = "SELECT COUNT(*) FROM commits WHERE author_id = ?1", nativeQuery = true)
    Long countCommitByAuthorId(Long authorId);

    @Query(value = "SELECT COUNT(*) FROM commits WHERE author_id = ?1 AND commits.commit_date BETWEEN ?2 AND ?3", nativeQuery = true)
    Long countCommitByAuthorId(Long authorId, @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT commits.* FROM commits WHERE project_id = ?1", nativeQuery = true)
    List<CommitEntity> commitByRepoId(Long repoId);

    @Query(value = "SELECT commits.* FROM commits WHERE project_id = ?1 AND commits.commit_date BETWEEN ?2 AND ?3", nativeQuery = true)
    List<CommitEntity> commitByRepoIdByDateRange(Long repoId, @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);


    @Query(value =
            "WITH commits_info AS (" +
                    "    SELECT " +
                    "        c.id AS commit_id, " +
                    "        c.author_id, " +
                    "        SUM(ch.lines_added + ch.lines_deleted) AS commit_size " +
                    "    FROM commits c " +
                    "    JOIN changes ch ON c.id = ch.commit_id " +
                    "    WHERE c.author_id = ?1 " +
                    "    GROUP BY c.id, c.author_id " +
                    ") " +
                    "SELECT " +
                    "    AVG(commit_size) AS average_commit_size " +
                    "FROM commits_info", nativeQuery = true)
    Integer findAverageCommitSizeForAuthor(Long authorId);


    @Query(value =
            "WITH commits_info AS (" +
                    "    SELECT " +
                    "        c.id AS commit_id, " +
                    "        c.author_id, " +
                    "        SUM(ch.lines_added + ch.lines_deleted) AS commit_size " +
                    "    FROM commits c " +
                    "    JOIN changes ch ON c.id = ch.commit_id " +
                    "    WHERE c.author_id = ?1 AND c.commit_date BETWEEN ?2 AND ?3 " +
                    "    GROUP BY c.id, c.author_id " +
                    ") " +
                    "SELECT " +
                    "    AVG(commit_size) AS average_commit_size " +
                    "FROM commits_info", nativeQuery = true)
    Integer findAverageCommitSizeForAuthorByDateRange(Long authorId, LocalDate startDate, LocalDate endDate);



    // Запрос для получения всех уникальных годов коммитов
    @Query(value = "SELECT DISTINCT EXTRACT(YEAR FROM commit_date) AS year FROM commits JOIN authors ON commits.author_id = authors.id  WHERE commits.project_id = ?1 ORDER BY year", nativeQuery = true)
    List<Integer> findAllYears(Long repoId);


    @Query(value = "SELECT (f.path || '/' || f.name) AS file_path, COUNT(c.id) AS change_count " +
            "FROM changes c " +
            "JOIN files f ON c.file_id = f.id " +
            "JOIN projects p ON f.project_id = p.id " +
            "WHERE p.id = ?1 " +
            "GROUP BY f.path, f.name", nativeQuery = true)
    List<Object[]> findMostActiveFiles(Long projectId);


    @Query(value = "SELECT ch.change_type, COUNT(*) AS change_count " +
            "FROM changes ch " +
            "JOIN commits cm ON ch.commit_id = cm.id " +
            "JOIN authors au ON cm.author_id = au.id " +
            "WHERE cm.project_id = ?1 " +
            "GROUP BY ch.change_type", nativeQuery = true)
    List<Object[]> findChangeCountsByRepositoryId(Long repositoryId);


    CommitEntity findByHash(String hash);
}
