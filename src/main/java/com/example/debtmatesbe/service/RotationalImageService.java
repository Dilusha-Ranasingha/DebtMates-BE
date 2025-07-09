package com.example.debtmatesbe.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.debtmatesbe.model.RotationalPayment;
import com.example.debtmatesbe.repo.RotationalPaymentRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class RotationalImageService {

    @Autowired
    private RotationalPaymentRepository paymentRepository;

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    private Cloudinary cloudinary;

    @PostConstruct
    public void init() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }

    public RotationalPayment uploadSlip(Long paymentId, MultipartFile file, Long userId) throws IOException {
        RotationalPayment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        if (!payment.getPayer().getId().equals(userId)) {
            throw new RuntimeException("Only the payer can upload a slip");
        }

        // Upload to Cloudinary
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        String imageUrl = uploadResult.get("url").toString();

        payment.setSlipUrl(imageUrl);
        payment.setStatus("Paid");
        payment.setPaidAt(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    public String getSlip(Long paymentId) {
        RotationalPayment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return payment.getSlipUrl();
    }
}