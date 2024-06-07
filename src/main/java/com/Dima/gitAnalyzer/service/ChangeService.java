package com.Dima.gitAnalyzer.service;

import com.Dima.gitAnalyzer.repository.CommitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChangeService {

    @Autowired
    private CommitRepository commitRepository;


    public Map<String, Integer> getChangeCountsByRepositoryId(Long repositoryId) {
        List<Object[]> results = commitRepository.findChangeCountsByRepositoryId(repositoryId);
        Map<String, Integer> resultMap = new HashMap<>();
        for (Object[] result : results) {
            String changeType = (String) result[0];
            Integer changeCount = ((Number) result[1]).intValue();
            resultMap.put(changeType, changeCount);
        }
        return resultMap;
    }
}
