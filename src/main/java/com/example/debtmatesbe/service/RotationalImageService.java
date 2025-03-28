package com.example.debtmatesbe.service;

import com.example.debtmatesbe.model.RotationalPayment;
import com.example.debtmatesbe.repo.RotationalPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RotationalImageService {

    @Autowired
    private RotationalPaymentRepository paymentRepository;

    public RotationalPayment uploadSlip(Long paymentId, byte[] slipData, Long userId) {
        RotationalPayment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        if (!payment.getPayer().getId().equals(userId)) {
            throw new RuntimeException("Only the payer can upload a slip");
        }
        payment.setSlip(slipData);
        payment.setStatus("Paid");
        payment.setPaidAt(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    public byte[] getSlip(Long paymentId) {
        RotationalPayment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return payment.getSlip();
    }
}