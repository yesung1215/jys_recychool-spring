package com.app.recychool.domain.entity;

import com.app.recychool.domain.type.PaymentStatus;
import com.app.recychool.domain.type.PaymentType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TBL_PAYMENT")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SequenceGenerator(
        name = "SEQ_PAYMENT_GENERATOR",
        sequenceName = "SEQ_PAYMENT",
        allocationSize = 1
)

public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_PAYMENT_GENERATOR")
    @EqualsAndHashCode.Include
    private Long id;
    private Integer paymentPrice;
    private String paymentUid;
    private String paymentPortOneNumber;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "RESERVE_ID")
//    private Reserve reserve;


}
