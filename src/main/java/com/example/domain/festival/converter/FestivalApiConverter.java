package com.example.domain.festival.converter;

import com.example.domain.festival.dto.external.FestivalApiItem;
import com.example.domain.festival.entity.Festival;
import com.example.domain.festival.entity.FestivalStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

//외부 DTO → 내부 엔티티 변환 클래스
@Component
public class FestivalApiConverter {
    //공공 API 날짜 문자열 포맷 ex. 20260430
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    // 외부 API DTO를 신규 Festival 엔티티로 변환(DB 초기 저장)
    public Festival toEntityFromListItem(FestivalApiItem item) {
        LocalDateTime startDate = parseStartDate(item.getEventstartdate());
        LocalDateTime endDate = parseEndDate(item.getEventenddate());

        return Festival.builder()
                .contentId(safeTrim(item.getContentid()))
                .title(safeTrim(item.getTitle()))
                .overview(defaultText(item.getOverview()))
                .contactNumber(nullableText(item.getTel()))
                .firstImageUrl(nullableText(item.getFirstimage()))
                .thumbnailUrl(resolveThumbnail(item.getFirstimage2(), item.getFirstimage()))
                .address(buildAddress(item.getAddr1(), item.getAddr2()))
                .homepageUrl(nullableText(item.getHomepage()))
                .startDate(startDate)
                .endDate(endDate)
                .mapX(parseDouble(item.getMapx()))
                .mapY(parseDouble(item.getMapy()))
                .lDongRegnCd(nullableText(item.getLDongRegnCd()))
                .status(calculateStatus(startDate, endDate))
                .build();
    }

    //기존 Festival 엔티티에 외부 API 값을 반영 (contendId 기준으로 이미 존재하는 축제를 Update할 때)
    public void updateFromListItem(Festival festival, FestivalApiItem item) {
        LocalDateTime startDate = parseStartDate(item.getEventstartdate());
        LocalDateTime endDate = parseEndDate(item.getEventenddate());

        festival.updateFestivalInfo(
                safeTrim(item.getTitle()),
                festival.getOverview(),
                nullableText(item.getTel()),
                nullableText(item.getFirstimage()),
                resolveThumbnail(item.getFirstimage2(), item.getFirstimage()),
                buildAddress(item.getAddr1(), item.getAddr2()),
                festival.getHomepageUrl(),
                startDate,
                endDate,
                parseDouble(item.getMapx()),
                parseDouble(item.getMapy()),
                nullableText(item.getLDongRegnCd()),
                calculateStatus(startDate, endDate)
        );
    }

    //상세 API 기반 상세 정보 보강
    public void updateDetailFields(Festival festival, FestivalApiItem item) {
        festival.updateFestivalDetailInfo(
                defaultText(item.getOverview()),
                nullableText(item.getHomepage()),
                nullableText(item.getTel())
        );
    }

    //시작일 문자열(yyyyMMdd)을 LocalDateTime 시작 시각으로 변환 ex. 20260430 -> 2026-04-30T00:00:00
    private LocalDateTime parseStartDate(String rawDate) {
        LocalDate date = LocalDate.parse(rawDate, DATE_FORMATTER);
        return date.atStartOfDay();
    }


    //종료일 문자열(yyyyMMdd)을 LocalDateTime 종료 시각으로 변환 ex. 20260503 -> 2026-05-03T23:59:59
    private LocalDateTime parseEndDate(String rawDate) {
        LocalDate date = LocalDate.parse(rawDate, DATE_FORMATTER);
        return LocalDateTime.of(date, LocalTime.of(23, 59, 59));
    }

    //문자열 좌표를 Double로 변환 ex. "128.872858180758" -> 128.872858180758
    private Double parseDouble(String value) {
        return Double.parseDouble(value);
    }

    //축제 상태 계산 (UPCOMING, ONGOING, ENDED)
    private FestivalStatus calculateStatus(LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(startDate)) {
            return FestivalStatus.UPCOMING;
        }

        if (now.isAfter(endDate)) {
            return FestivalStatus.ENDED;
        }
        return FestivalStatus.ONGOING;
    }

     //주소 합치기 (addr1 + addr2)
    private String buildAddress(String addr1, String addr2) {
        String base = safeTrim(addr1);
        String detail = nullableText(addr2);

        if (detail == null) {
            return base;
        }
        return base + " " + detail;
    }

    //이미지 URL 걸정 (firstimage2가 있으면 썸네일로 사용, 없으면 firstimage를 대체 사용)
    private String resolveThumbnail(String firstimage2, String firstimage) {
        String second = nullableText(firstimage2);
        if (second != null) {
            return second;
        }
        return nullableText(firstimage);
    }

    //overview 같은 필드가 비어 있으면 기본 텍스트로 대체, Festival 엔티티에서 overview는 not null이기 때문
    private String defaultText(String value) {
        String text = nullableText(value);
        return text != null ? text : "상세 설명 없음";
    }


    //nullableText 및 safeTrim 메서드는 API 데이터를 안정적으로 저장하기 위함

    //nullableText(): null => null, 공백문자열(" ") => null 처리 역할
    private String nullableText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    //safeTrim(): null 및 공백이면 예외, 아니면 trim해서 반환하는 역할
    private String safeTrim(String value) {
        if (value == null) {
            throw new IllegalArgumentException("필수 값이 null 입니다.");
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("필수 값이 비어 있습니다.");
        }
        return trimmed;
    }
}
