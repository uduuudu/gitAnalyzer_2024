package com.Dima.gitAnalyzer.service;

import com.Dima.gitAnalyzer.entity.*;
import com.Dima.gitAnalyzer.repository.AuthorRepository;
import com.Dima.gitAnalyzer.repository.ChangeRepository;
import com.Dima.gitAnalyzer.repository.CommitRepository;
import com.Dima.gitAnalyzer.repository.FileRepository;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.*;

@Service
public class CommitService {

    @Autowired
    private CommitRepository commitRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private ChangeRepository changeRepository;


    public void processCommit(RevCommit commit, ProjectEntity repository, Repository repo)  {

        AuthorEntity author = authorRepository.findByNameAndProjectId(commit.getAuthorIdent().getName(),
                        repository.getId())
                .orElseGet(() -> {
                    AuthorEntity newAuthor = new AuthorEntity();
                    newAuthor.setName(commit.getAuthorIdent().getName());
                    return authorRepository.save(newAuthor);
                });


        CommitEntity newCommitEntity = new CommitEntity();
        newCommitEntity.setMessage(commit.getFullMessage());
        newCommitEntity.setCommitDate(LocalDateTime.ofInstant(commit.getAuthorIdent().getWhen().toInstant(), ZoneId.systemDefault()));
        newCommitEntity.setAuthor(author);
        newCommitEntity.setHash(commit.getId().getName());
        newCommitEntity.setRepositoryEntity(repository);
        commitRepository.save(newCommitEntity);


        if (commit.getParentCount() == 0) {
            return;
        }

        RevCommit parent = commit.getParent(0);


        try (RevWalk revWalk = new RevWalk(repo)) {
            parent = revWalk.parseCommit(parent.getId());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        try (TreeWalk treeWalk = new TreeWalk(repo)) {
            treeWalk.addTree(commit.getTree());
            treeWalk.addTree(parent.getTree());
            treeWalk.setRecursive(true);
            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            newTreeIter.reset(repo.newObjectReader(), commit.getTree());
            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            oldTreeIter.reset(repo.newObjectReader(), parent.getTree());

            List<DiffEntry> diffs = new Git(repo).diff()
                    .setNewTree(newTreeIter)
                    .setOldTree(oldTreeIter)
                    .call();

            for (DiffEntry diff : diffs) {
                processDiffEntry(diff, newCommitEntity, repo);
            }
        } catch (GitAPIException | IOException e ) {
            System.out.println(e.getMessage());

        }

    }

    private void processDiffEntry(DiffEntry diff, CommitEntity commitEntity, Repository repo) {
        String changeType;
        int linesAdded = 0;
        int linesDeleted = 0;

        switch (diff.getChangeType()) {
            case ADD:
                changeType = "add";
                break;
            case DELETE:
                changeType = "delete";
                break;
            case MODIFY:
                changeType = "modify";
                break;
            case RENAME:
                changeType = "rename";
                break;
            default:
                changeType = "unknown";
                break;
        }

        String filePath;
        if ("/dev/null".equals(diff.getOldPath())) {
            filePath = diff.getNewPath();
        } else if ("/dev/null".equals(diff.getNewPath())) {
            filePath = diff.getOldPath();
        } else {
            filePath = diff.getNewPath();
        }


        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             DiffFormatter formatter = new DiffFormatter(out)) {
            formatter.setRepository(repo);
            for (Edit edit : formatter.toFileHeader(diff).toEditList()) {
                linesAdded += edit.getEndB() - edit.getBeginB();
                linesDeleted += edit.getEndA() - edit.getBeginA();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        FileEntity fileEntity = new FileEntity();
        fileEntity.setProjectId(commitEntity.getRepositoryEntity().getId());

        int lastSlashIndex = filePath.lastIndexOf('/');
        if (lastSlashIndex == -1) {
            fileEntity.setName(filePath);
            fileEntity.setPath("");
        }
        else {
            String path = filePath.substring(0, lastSlashIndex);
            String fileName = filePath.substring(lastSlashIndex + 1);
            fileEntity.setName(fileName);
            fileEntity.setPath(path);
        }

        fileEntity = fileRepository.save(fileEntity);

        ChangeEntity changeEntity = new ChangeEntity();
        changeEntity.setCommitId(commitEntity.getId());
        changeEntity.setFileId(fileEntity.getId());
        changeEntity.setChangeType(changeType);
        changeEntity.setLinesAdded(linesAdded);
        changeEntity.setLinesDeleted(linesDeleted);
        changeRepository.save(changeEntity);
    }



    // Метод для получения всех уникальных годов коммитов
    public List<Integer> getAllYears(Long repositoryId) {
        return commitRepository.findAllYears(repositoryId);
    }


    public Map<String, Integer> getCommitsByDay(Long repositoryId) {
        List<CommitEntity> commitEntities = commitRepository.commitByRepoId(repositoryId);
        Map<String, Integer> commitsByDay = new TreeMap<>();

        for (CommitEntity commitEntity : commitEntities) {
            String day = commitEntity.getCommitDate().toLocalDate().toString();
            commitsByDay.put(day, commitsByDay.getOrDefault(day, 0) + 1);
        }
        return commitsByDay;
    }

    public Map<String, Integer> getCommitsByDayByDateRange(Long repositoryId, LocalDate start, LocalDate end) {
        List<CommitEntity> commitEntities = commitRepository.commitByRepoIdByDateRange(repositoryId, start, end);
        Map<String, Integer> commitsByDayByDateRange = new TreeMap<>();

        for (CommitEntity commitEntity : commitEntities) {
            String day = commitEntity.getCommitDate().toLocalDate().toString();
            commitsByDayByDateRange.put(day, commitsByDayByDateRange.getOrDefault(day, 0) + 1);
        }
        return commitsByDayByDateRange;
    }

    public Map<Month, Integer> getCommitsByYear(Long repositoryId, int year) {
        List<CommitEntity> commitEntities = commitRepository.commitByRepoId(repositoryId);
        Map<Month, Integer> commitsByMonth = new TreeMap<>();

        for (CommitEntity commitEntity : commitEntities) {
            if (commitEntity.getCommitDate().getYear() == year) {
                Month month = commitEntity.getCommitDate().getMonth();
                commitsByMonth.put(month, commitsByMonth.getOrDefault(month, 0) + 1);
            }
        }
        return commitsByMonth;
    }

    public Map<String, Long> getCommitStatisticsByRepository(Long repositoryId) {
        Map<String, Long> commitStatistics = new HashMap<>();
        List<AuthorEntity> authors = authorRepository.findByRepository(repositoryId);

        for (AuthorEntity author : authors) {
            commitStatistics.put(author.getName(), commitRepository.countCommitByAuthorId(author.getId()));
        }
        return commitStatistics;

    }

    public Map<String, Integer> getAvgCommSizeByRepository(Long repositoryId) {
        Map<String, Integer> commitStatistics = new HashMap<>();
        List<AuthorEntity> authors = authorRepository.findByRepository(repositoryId);

        for (AuthorEntity author : authors) {
            commitStatistics.put(author.getName(), commitRepository.findAverageCommitSizeForAuthor(author.getId()));
        }
        return commitStatistics;

    }

    public Map<String, Integer> getAvgCommSizeByRepositoryByRange(Long repositoryId, LocalDate start, LocalDate end) {
        Map<String, Integer> commitStatistics = new HashMap<>();
        List<AuthorEntity> authors = authorRepository.findByRepository(repositoryId);

        for (AuthorEntity author : authors) {
            commitStatistics.put(author.getName(), commitRepository.findAverageCommitSizeForAuthorByDateRange(author.getId(), start, end));
        }
        return commitStatistics;

    }

    // Новое

    public Map<String, Long> getCommitStatisticsByRepositoryAndDateRange(Long repositoryId, LocalDate start, LocalDate end) {
        Map<String, Long> commitStatistics = new HashMap<>();
        List<AuthorEntity> authors = authorRepository.findByRepository(repositoryId);

        for (AuthorEntity author : authors) {
            commitStatistics.put(author.getName(), commitRepository.countCommitByAuthorId(author.getId(), start, end));

        }
        return commitStatistics;
    }


}

