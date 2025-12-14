package com.app.recychool.domain.dto;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCompleteResponseDTO {

    private String paymentId;  // 포트원 결제 번호 (paymentUid)
    private Long reserveId;    // 어떤 예약에 대한 결제인지

}
