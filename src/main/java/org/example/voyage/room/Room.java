package org.example.voyage.room;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.voyage.hotel.Hotel;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "rooms", uniqueConstraints = @UniqueConstraint(columnNames = {"hotel_id", "number"}))
public class Room {
    @Id
    @UuidGenerator
    private UUID id;
    @Column(nullable = false)
    private String number;
    @Column(nullable = false)
    private int floor;
    @Column(nullable = false)
    private int capacity;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerNight;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType type;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status = RoomStatus.AVAILABLE;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    public enum RoomStatus {
        AVAILABLE,
        BOOKED,
        MAINTENANCE
    }
    public enum RoomType {
        SINGLE,
        DOUBLE,
        SUITE
    }
}
