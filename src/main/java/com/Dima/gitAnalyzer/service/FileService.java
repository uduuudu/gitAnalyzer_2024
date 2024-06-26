package com.Dima.gitAnalyzer.service;

import com.Dima.gitAnalyzer.repository.CommitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileService {
    @Autowired
    private CommitRepository commitRepository;

    public Map<String, Integer> getMostActiveFiles(Long projectId) {
        List<Object[]> results = commitRepository.findMostActiveFiles(projectId);
        Map<String, Integer> activeFilesMap = new HashMap<>();

        for (Object[] result : results) {
            String filePath = (String) result[0];
            Integer changeCount = ((Number) result[1]).intValue();
            activeFilesMap.put(filePath, changeCount);
        }
        return activeFilesMap;
    }
}
