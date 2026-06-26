package org.example.voyage.booking;

import jakarta.validation.Valid;
import org.example.voyage.booking.dto.*;
import org.example.voyage.payment.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class BookingController {
    private final BookingService bookingService;
    private final PaymentService paymentService;

    public BookingController(BookingService bookingService, PaymentService paymentService) {
        this.bookingService = bookingService;
        this.paymentService = paymentService;
    }

    @PostMapping("/bookings")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody BookingRequest booking, @AuthenticationPrincipal UserDetails userDetails) {
        BookingResponse bookingResponse = bookingService.create(booking, userDetails);
        paymentService.pay(bookingResponse.getId(), userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingResponse);
    }

    @DeleteMapping("/bookings")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> cancel(UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        bookingService.cancel(id, userDetails);
        paymentService.refund(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/bookings")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<BookingResponse>> getAllBookings(@Valid Pagination pagination,  @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.getMyBookings(pagination, userDetails));
    }

    @GetMapping("/bookings/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.getMyBookingById(id, userDetails));
    }

    @GetMapping("/hotels/{hotelId}/bookings")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<ManagerBookingResponse>> getAllHotelBookings(BookingSearchCriteria criteria, @PathVariable UUID hotelId, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.getHotelBookings(criteria, hotelId, userDetails));
    }

    @GetMapping("/hotels/{hotelId}/bookings/{bookingId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ManagerBookingResponse> getHotelBookingById(@PathVariable UUID hotelId, @PathVariable UUID bookingId, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.getBookingById(hotelId, bookingId, userDetails));
    }
}
