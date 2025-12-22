package com.app.recychool.domain.dto;


import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class PaymentPageResponseDTO {

    // Reserve
    private Long reserveId;
    private String reserveType;
    private String startDate;

    // 결제 금액
    private Integer amount;

    private String userName;
    private String userEmail;
    private String userPhone;
    private String schoolName;
    private String schoolAddress;

}
