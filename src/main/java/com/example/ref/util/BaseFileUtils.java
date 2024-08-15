package com.example.ref.util;

import com.example.ref.dto.FileDto;
import com.example.ref.rules.FileType;
import org.springframework.web.multipart.MultipartFile;

public abstract class BaseFileUtils {

    public abstract FileDto uploadFile(String realPath, MultipartFile file, Long fileSize, FileType fileType) throws Exception;
    public abstract void deleteFile(String filePath);

}
