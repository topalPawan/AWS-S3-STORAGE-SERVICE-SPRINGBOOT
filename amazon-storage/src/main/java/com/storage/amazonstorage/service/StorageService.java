package com.storage.amazonstorage.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StorageService implements StorageServiceInterface{

    @Value("${application.bucket.name}")
    private String bucketName;

    @Autowired
    public AmazonS3 amazonS3;

    @Override
    public String createBucket(String bucketName) {
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            Bucket bucket = amazonS3.createBucket(bucketName);
            return bucket.getName();
        }else {
            return bucketName;
        }
    }



//    public String createFolder(String bucketName, String folderName) {
//        if (!amazonS3.doesBucketExistV2(bucketName)) {
//            throw new RuntimeException("Bucket does not exist: " + bucketName);
//        }
//
//        ObjectMetadata metadata = new ObjectMetadata();
//        metadata.setContentLength(0);
//
//        InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
//
//        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
//                folderName + "/", emptyContent, metadata);
//
//        amazonS3.putObject(putObjectRequest);
//
//        return folderName;
//
//    }

    @Override
    public String saveFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        try {
            File file1 = convertMultiPartFileToFile(file);
            PutObjectResult putObjectResult = amazonS3.putObject(bucketName, originalFilename, file1);
            return putObjectResult.getContentMd5();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] downloadFile(String fileName) {

        S3Object object = amazonS3.getObject(bucketName, fileName);
        S3ObjectInputStream objectContent = object.getObjectContent();
        try {
            byte[] byteArray = IOUtils.toByteArray(objectContent);
            return byteArray;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String deleteFile(String fileName) {

        amazonS3.deleteObject(bucketName, fileName);
        return "File Deleted";
    }

    @Override
    public List<String> listAllFiles() {

        ListObjectsV2Result listObjectsV2Result = amazonS3.listObjectsV2(bucketName);
        return listObjectsV2Result.getObjectSummaries().stream().map(S3ObjectSummary::getKey).collect(Collectors.toList());

    }

    private File convertMultiPartFileToFile(MultipartFile file) throws IOException{
        File converFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(converFile);
        fos.write(file.getBytes());
        fos.close();
        return converFile;
    }

}