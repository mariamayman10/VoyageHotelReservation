package org.example.voyage.payment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.voyage.booking.Booking;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "payments")
public class Payment {
    @Id @UuidGenerator
    private UUID id;
    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;
    @CreationTimestamp
    private LocalDateTime paidAt;
    public enum PaymentStatus {
        PENDING, COMPLETED, CANCELLED
    }
    public enum PaymentMethod {
        CREDIT, CASH
    }
}