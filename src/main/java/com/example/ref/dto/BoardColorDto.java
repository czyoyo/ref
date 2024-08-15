package com.example.ref.dto;

import com.example.ref.entity.BoardColor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardColorDto {

    private Long id;

    private String title;

    private String color;


    private static BoardColorDto.BoardColorDtoBuilder boardColorDtoBuilder(BoardColorDto boardColorDto){
        return BoardColorDto.builder()
            .id(boardColorDto.getId())
            .title(boardColorDto.getTitle())
            .color(boardColorDto.getColor());
    }

    public static BoardColorDto convertToDto(BoardColor boardColor){
        return BoardColorDto.builder()
            .id(boardColor.getId())
            .title(boardColor.getTitle())
            .color(boardColor.getColor())
            .build();
    }



}
