package com.example.domain.festival.dto.external;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter //Test용도 (FestivalApiConverterTest)
@NoArgsConstructor
public class FestivalApiItem {
    private String contentid;
    private String title;
    private String tel;
    private String addr1;
    private String addr2;
    private String homepage;
    private String overview;
    private String firstimage;
    private String firstimage2;
    private String mapx;
    private String mapy;
    private String lDongRegnCd;
    private String eventstartdate;
    private String eventenddate;
    private String modifiedtime; //수정시간, INCREMENTAL 동기화 고려 시 참고 가능
}
