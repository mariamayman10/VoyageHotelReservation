package org.example.voyage.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationPayload implements Serializable {
    private UUID bookingId;
    private String guestName;
    private String guestEmail;
    private String hotelName;
    private String checkIn;
    private String checkOut;
    private String eventType;
    private LocalDateTime timestamp;
}
