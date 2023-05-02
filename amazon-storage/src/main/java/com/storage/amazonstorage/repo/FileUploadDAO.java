package com.storage.amazonstorage.repo;

import com.storage.amazonstorage.entity.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileUploadDAO extends JpaRepository<FileUpload, Long> {
    void deleteByFileName(String fileName);
}

