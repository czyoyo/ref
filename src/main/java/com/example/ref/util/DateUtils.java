package com.example.ref.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DateUtils {

    // month 를 받아서 첫날 반환
    public static LocalDateTime getFirstDayOfMonth(int month) {
        return LocalDateTime.of(LocalDate.now().getYear(), month, 1, 0, 0, 0);
    }

    // month 를 받아서 마지막 날 반환
    public static LocalDateTime getLastDayOfMonth(int month) {
        return LocalDateTime.of(LocalDate.now().getYear(), month, LocalDate.now().lengthOfMonth(), 23, 59, 59);
    }

    // day 를 받아서 00시 00분 00초 반환
    public static LocalDateTime getStartOfDay(int day) {
        return LocalDateTime.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), day, 0, 0, 0);
    }

    // day 를 받아서 23시 59분 59초 반환
    public static LocalDateTime getEndOfDay(int day) {
        return LocalDateTime.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), day, 23, 59, 59);
    }

    // 해당 일정이 자정을 넘어 가는지 체크
    public static boolean isOverMidnight(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return startDateTime.getDayOfMonth() != endDateTime.getDayOfMonth();
    }

    // get duration in minutes
    public static long getDurationInMinutes(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return ChronoUnit.MINUTES.between(startDateTime, endDateTime);
    }




}
