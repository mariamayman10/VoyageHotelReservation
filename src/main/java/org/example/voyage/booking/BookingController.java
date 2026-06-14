package org.example.voyage.booking;

import jakarta.validation.Valid;
import org.example.voyage.booking.dto.BookingRequest;
import org.example.voyage.booking.dto.BookingResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody BookingRequest booking, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.create(booking, userDetails));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> cancel(UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        bookingService.cancel(id, userDetails);
        return ResponseEntity.noContent().build();
    }
}
