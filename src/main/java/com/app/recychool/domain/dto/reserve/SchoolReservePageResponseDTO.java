package com.app.recychool.domain.dto.reserve;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolReservePageResponseDTO {

    // 학교 기본 정보
    private Long schoolId;
    private String schoolName;
    private String schoolAddress;
    private String schoolPhone;

    // 예약 정보 (URL에 따라 달라짐)
    private String reserveType;   // PLACE or PARKING
    private String usageTime;     // "09:00 ~ 17:00" / "18:00 ~ 08:00"
    private Integer price;        // PLACE = 0, PARKING = 30000
    private Integer deposit;      // PLACE = 50000, PARKING = 0

    // 캘린더 제어
    private List<LocalDate> unavailableDates;
}
