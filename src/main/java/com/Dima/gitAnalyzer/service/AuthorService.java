package com.Dima.gitAnalyzer.service;

import com.Dima.gitAnalyzer.entity.*;
import com.Dima.gitAnalyzer.repository.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.eclipse.jgit.revwalk.RevWalk;


@Service
public class AuthorService {
    @Autowired
    private CommitRepository commitRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private ChangeRepository changeRepository;

    @Autowired
    private AuthorRepository authorRepository;

}
