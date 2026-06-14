package org.example.voyage.booking.dto;

import lombok.*;
import org.example.voyage.booking.Booking;
import org.example.voyage.room.dto.RoomResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private UUID id;
    private RoomResponse room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BigDecimal totalPrice;
    private Booking.BookingStatus status;
}
