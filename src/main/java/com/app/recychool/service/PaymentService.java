package com.app.recychool.service;

import com.app.recychool.domain.entity.Payment;

import java.util.List;

public interface PaymentService {

    public List<Payment> findByUserId(Long userId);

}
