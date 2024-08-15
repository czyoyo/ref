package com.example.ref.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RefreshSocketDto {

    @Schema(description = "타입", example = "notice")
    private String type; // notice, impression
    @Schema(description = "메시지", example = "안녕하세요. 반갑습니다.")
    private String message; // free text
    @Schema(description = "유저 아이디", example = "1")
    private String userId; // user id
    @Schema(description = "액션", example = "create")
    private String action; // create, update, delete
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "날짜", example = "2024-01-01 00:00:00")
    private LocalDateTime dateTime; // LocalDateTime.now()
    @Schema(description = "세션", example = "agjsioahjogiahgoia")
    private String session;

    RefreshSocketDto() {
        this.dateTime = LocalDateTime.now();
    }

}
