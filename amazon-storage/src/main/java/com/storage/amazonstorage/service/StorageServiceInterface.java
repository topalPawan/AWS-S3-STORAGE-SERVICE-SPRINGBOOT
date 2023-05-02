package com.storage.amazonstorage.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StorageServiceInterface {
    public String createBucket(String bucketName);

    String saveFile(MultipartFile file);

    public byte[] downloadFile(String fileName);

    public String deleteFile(String fileName);

    public List<String> listAllFiles();
}
