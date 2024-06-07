package com.Dima.gitAnalyzer.service;

import com.Dima.gitAnalyzer.entity.ProjectEntity;
import com.Dima.gitAnalyzer.repository.CommitRepository;
import com.Dima.gitAnalyzer.repository.ProjectRepository;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;


@Service
public class ProjectService {

    @Autowired
    private ProjectRepository repositoryRepository;

    @Autowired
    private CommitService commitService;

    @Autowired
    CommitRepository commitRepository;

    public void registerRepository(String repositoryUrl, String path, String name, String branch) {
        try {
            Git git = Git.cloneRepository()
                    .setURI(repositoryUrl)
                    .setDirectory(new File(path))
                    .call();
            Repository repo = git.getRepository();
            if (!(branch.equals("master"))) {
                git.checkout().setName("refs/remotes/origin/" + branch).call();
            } else {
                git.checkout().setName(branch).call();
            }
            ProjectEntity repositoryEntity = repositoryRepository.findByUrl(repositoryUrl)
                    .orElseGet(() -> {
                        ProjectEntity newRepository = new ProjectEntity();
                        newRepository.setUrl(repositoryUrl);
                        newRepository.setName(name);
                        newRepository.setDirectory(path);
                        newRepository.setAnalyze_branch(branch);
                        return repositoryRepository.save(newRepository);
                    });
            git.log().call().forEach(commit ->
                    commitService.processCommit(commit, repositoryEntity, repo));
            git.close();

        } catch (GitAPIException e) {
            System.out.println(e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 0 */30 * ?")
    public void updateRepository() {
        List<ProjectEntity> repositories = repositoryRepository.findAll();
        for (ProjectEntity repositoryEntity : repositories) {
            String repositoryPath = repositoryEntity.getDirectory();
            try (Git git = Git.open(new File(repositoryPath))) {
                git.fetch().call();
                if (!(repositoryEntity.getAnalyze_branch().equals("master"))) {
                    git.checkout().setName("refs/remotes/origin/" + repositoryEntity.getAnalyze_branch()).call();
                } else {
                    git.checkout().setName(repositoryEntity.getAnalyze_branch()).call();
                }
                Iterable<RevCommit> commits = git.log().call();
                for (RevCommit commit : commits) {
                    if (commitRepository.findByHash(commit.getId().getName()) == null) {
                        commitService.processCommit(commit, repositoryEntity, git.getRepository());
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }
}



