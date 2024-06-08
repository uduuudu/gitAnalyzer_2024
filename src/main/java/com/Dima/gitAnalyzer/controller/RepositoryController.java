package com.Dima.gitAnalyzer.controller;

import com.Dima.gitAnalyzer.entity.ProjectEntity;
import com.Dima.gitAnalyzer.repository.ProjectRepository;
import com.Dima.gitAnalyzer.service.ChangeService;
import com.Dima.gitAnalyzer.service.CommitService;
import com.Dima.gitAnalyzer.service.FileService;
import com.Dima.gitAnalyzer.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;



@Controller
public class RepositoryController {
    @Autowired
    CommitService commitService;

    @Autowired
    FileService fileService;

    @Autowired
    ChangeService changeService;

    @Autowired
    ProjectService projectService;

    @Autowired
    ProjectRepository projectRepository;



    @GetMapping("/")
    public String startPage() {
        return "redirect:/login";
    }

    @GetMapping("/home")
    public String showHomePage(Model model) {
        List<ProjectEntity> repositories = projectRepository.findAll();
        model.addAttribute("repositories", repositories);
        return "home";
    }

    @GetMapping("/statistic/{projectId}")
    public String showVisualization(@PathVariable Long projectId, Model model) {
        Map<String, Integer> commitsByDay = commitService.getCommitsByDay(projectId);
        model.addAttribute("commitsByDay", commitsByDay);
        Map<String, Long> commitCountMap = commitService.getCommitStatisticsByRepository(projectId);
        model.addAttribute("commitCountMap", commitCountMap);
        Map<String, Integer> avgSizeCommitsMap = commitService.getAvgCommSizeByRepository(projectId);
        model.addAttribute("avgSizeCommitsMap", avgSizeCommitsMap);
        List<Integer> years = commitService.getAllYears(projectId);
        model.addAttribute("years", years);
        model.addAttribute("projectId", projectId);

        Map<String, Integer> fileActivity = fileService.getMostActiveFiles(projectId);
        model.addAttribute("fileActivity", fileActivity);
        Map<String, Integer> changeDistr = changeService.getChangeCountsByRepositoryId(projectId);
        model.addAttribute("changeDistr", changeDistr);

        return "visualization";
    }


    @GetMapping("/repositoryRegistration")
    public String showRegistrationForm() {
        return "repositoryRegistration";
    }

    @PostMapping("/repositoryRegistration")
    public String showRegistrationForm(@RequestParam String url,
                                       @RequestParam String directory,
                                       @RequestParam String name,
                                       @RequestParam String branch,
                                       Model model) {
        projectService.registerRepository(url, directory, name, branch);
        return "redirect:/home";
    }


    @DeleteMapping("/deleteRepository/{id}")
    public ResponseEntity<Void> deleteRepository(@PathVariable Long id) {
        projectService.deleteRepositoryById(id);
        return ResponseEntity.noContent().build();
    }

}
