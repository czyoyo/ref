package com.example.ref.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommonUtils {

    // null check 를 하는데 null 이면 true, 아니면 false
    // 파일 업로드시 파일이 없을 경우 null check 를 위해 사용
    public static boolean isMultipartFileEmpty(MultipartFile multipartFile) {
        return multipartFile == null || multipartFile.isEmpty();
    }


}
