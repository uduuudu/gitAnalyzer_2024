package com.Dima.gitAnalyzer.controller;

import com.Dima.gitAnalyzer.entity.ProjectEntity;
import com.Dima.gitAnalyzer.repository.CommitRepository;
import com.Dima.gitAnalyzer.repository.ProjectRepository;
import com.Dima.gitAnalyzer.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@Controller
public class CommitController {


    @Autowired
    CommitRepository commitRepository;
    @Autowired
    ProjectRepository repositoryRepository;
    @Autowired
    CommitService commitService;

    @Autowired
    FileService fileService;

    @Autowired
    ChangeService changeService;

    @Autowired
    ProjectService repositoryService;

    @Autowired
    AuthorService authorService;


    @GetMapping("/{projectId}/years")
    public ResponseEntity<List<Integer>> getAllYears(@PathVariable Long projectId) {
        List<Integer> years = commitService.getAllYears(projectId);
        return ResponseEntity.ok().body(years);
    }



    //////////////////////////////////////////
    // 1 мая
    //////////////////////////////////////////





    //27.04.2024
    @GetMapping("/getTableDataByDateRange")
    public ResponseEntity<Map<String, Long>> getCommitsByDateRange(@RequestParam("startDate") LocalDate startDate,
                                              @RequestParam("endDate") LocalDate endDate, @RequestParam("projectId") Long projectId) {
        Map<String, Long> myMap = commitService.getCommitStatisticsByRepositoryAndDateRange(projectId, startDate, endDate);
        return ResponseEntity.ok(myMap);
    }

    @GetMapping("/getAvgTableDataByDateRange")
    public ResponseEntity<Map<String, Integer>> getCommitsByDateRangeAvg(@RequestParam("startDateAvg") LocalDate startDate,
                                                                   @RequestParam("endDateAvg") LocalDate endDate, @RequestParam("projectId") Long projectId) {
        Map<String, Integer> myMap = commitService.getAvgCommSizeByRepositoryByRange(projectId, startDate, endDate);
        return ResponseEntity.ok(myMap);
    }


    @GetMapping("/getChartData")
    public ResponseEntity<Map<Month, Integer>> getChartData(@RequestParam("selectedYear") int year,
                                                            @RequestParam("projectId") Long projectId) {
        Map<Month, Integer> commitsByYear = commitService.getCommitsByYear(projectId, year);

        return ResponseEntity.ok(commitsByYear);
    }

    @GetMapping("/getChartDataByDateRange")
    public ResponseEntity<Map<String, Integer>> getChartData(@RequestParam("projectId") Long projectId,
                                                            @RequestParam("startDate") LocalDate startDate,
                                                            @RequestParam("endDate") LocalDate endDate) {
        Map<String, Integer> commitsByRange = commitService.getCommitsByDayByDateRange(projectId, startDate, endDate);

        return ResponseEntity.ok(commitsByRange);
    }


}
