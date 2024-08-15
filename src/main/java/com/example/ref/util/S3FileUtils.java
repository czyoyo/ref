package com.example.ref.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.ref.dto.FileDto;
import com.example.ref.rules.FileType;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j
@RequiredArgsConstructor
public class S3FileUtils extends BaseFileUtils {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public FileDto uploadFile(String realPath, MultipartFile file, Long fileSize, FileType fileType)
        throws IOException {

        if(file.isEmpty()) {
            return null;
        }

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String randomString = FileUtils.getRandomString();

        // 파일 사이즈 체크
        if(file.getSize() < 1) {
            return null;
        } else if(file.getSize() > 1024 * 1024 * fileSize) {
            throw new IllegalArgumentException("파일 사이즈는 " + fileSize + "MB 이하로 업로드 가능합니다.");
        }

        String realFileName = file.getOriginalFilename();

        // 파일명 체크
        if(realFileName == null || realFileName.isEmpty()) {
            throw new IllegalArgumentException("파일명이 존재하지 않습니다.");
        }

        // 확장자 체크
        String extension = realFileName.substring(realFileName.lastIndexOf(".") + 1).toLowerCase();

        if(!StringUtils.hasText(extension)){
            throw new IllegalArgumentException("파일 확장자가 존재하지 않습니다.");
        }

        // 확장자 체크
        if(!extension.equals("jpg") && !extension.equals("jpeg") && !extension.equals("png") && !extension.equals("gif") && !extension.equals("svg")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }

        // 파일명 특수문자 체크
        if(realFileName.contains("..") || realFileName.contains("/") || realFileName.contains("\\")) {
            throw new IllegalArgumentException("파일명에 특수문자가 포함되어 있습니다.");
        }

        try{
            // 파일명 중복 방지(난수)
            String savedFileName = System.currentTimeMillis() + "_" + randomString + "." + extension;

            // 파일 업로드
            InputStream inputStream = file.getInputStream();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            String savedFilePath = "%s/%s/%s/%s".formatted(realPath,fileType.getPath(), today, savedFileName);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, savedFilePath, inputStream, metadata);
            amazonS3Client.putObject(putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead));
            String uploadedFileUrl = amazonS3Client.getUrl(bucketName, savedFilePath).toString();

            // S3 업로드
            return FileDto.builder()
                .originFileName(realFileName)
                .savedFileName(savedFileName)
                .fileSize(file.getSize())
                .filePath(savedFilePath)
                .fileExtension(extension)
                .url(uploadedFileUrl)
                .build();

        } catch (Exception e) {
            log.info("파일 업로드 실패: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void deleteFile(String filePath) {
        amazonS3Client.deleteObject(bucketName, filePath);
    }


}
