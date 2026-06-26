package org.example.voyage.booking.dto;

import lombok.*;
import org.example.voyage.booking.Booking;
import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingSearchCriteria {
    private Booking.BookingStatus status;
    private LocalDate from;
    private LocalDate to;
    private int page = 0;
    private int size = 10;
}
