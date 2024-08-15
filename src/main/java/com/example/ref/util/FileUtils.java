package com.example.ref.util;

import com.example.ref.dto.FileDto;
import com.example.ref.rules.FileType;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j
@RequiredArgsConstructor
public class FileUtils extends BaseFileUtils {


    public static boolean checkLimitSize(MultipartFile imageFile, long megabyte) {
        return imageFile.getSize() > megabyte * 1024 * 1024;
    }

    public static String getRandomString() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    // 파일 삭제
    @Override
    public void deleteFile(String filePath) {
        File file = new File(filePath);
        if(file.exists()) {
            file.delete();
        } else {
            log.info("파일이 존재하지 않습니다. {} ", filePath);
        }
    }

    @Override
    public FileDto uploadFile(String realPath, MultipartFile file, Long fileSize, FileType fileType) throws Exception {

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        if(file.isEmpty()) {
            return null;
        }

        // 파일 저장 경로
        String filePath = "%s/%s/%s".formatted(realPath, fileType.getPath(), today);

        File dir = new File(filePath);
        // 디렉토리가 없으면 생성
        if(!dir.exists()) {
            dir.mkdirs();
        }

        // 파일 사이즈 체크
        if(file.getSize() <1) {
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

        // 파일명 중복 방지
        String savedFileName = System.currentTimeMillis() + "_" + getRandomString() + "." + extension;

        // 파일 저장
        File saveFile = new File(filePath, savedFileName);

        // 파일 저장 경로 (real path + 파일 저장 경로 + 오늘날짜 + 파일명)
        String savedFilePath = "%s/%s/%s/%s".formatted(realPath, fileType.getPath(), today, savedFileName);

        // Dto 에 저장
        FileDto fileDto = FileDto.builder()
            .originFileName(realFileName)
            .savedFileName(savedFileName)
            .fileSize(file.getSize())
            .filePath(savedFilePath)
            .fileExtension(extension)
            .build();

        try {
            // 파일 저장
            file.transferTo(saveFile);
            // 파일 정보 저장
            return fileDto;
        } catch (Exception e) {
            log.info("파일 업로드 실패 {}", e);
            throw new IllegalArgumentException("파일 업로드 실패");
        }

    }

}
