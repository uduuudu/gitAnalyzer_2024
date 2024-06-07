package com.Dima.gitAnalyzer.repository;

import com.Dima.gitAnalyzer.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileEntity, Long> {

}
