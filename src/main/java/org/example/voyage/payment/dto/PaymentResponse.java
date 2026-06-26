package org.example.voyage.payment.dto;

import org.example.voyage.payment.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentResponse {
    private UUID id;
    private UUID bookingId;
    private BigDecimal amount;
    private String currency;
    private Payment.PaymentStatus status;
    private LocalDateTime transactedAt;
}