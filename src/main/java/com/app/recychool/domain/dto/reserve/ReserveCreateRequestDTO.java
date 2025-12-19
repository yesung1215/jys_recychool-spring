package com.app.recychool.domain.dto.reserve;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReserveCreateRequestDTO {

    private LocalDate startDate; // 선택한 날짜
}
