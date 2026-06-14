package org.example.voyage.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.voyage.booking.CheckOutAfterCheckIn;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CheckOutAfterCheckIn
public class BookingRequest {
    @NotNull(message = "Room id must be provided")
    private UUID roomId;
    @NotNull(message = "Check in date must be provided")
    private LocalDate checkInDate;
    @NotNull(message = "Check out date must be provided")
    private LocalDate checkOutDate;
}
