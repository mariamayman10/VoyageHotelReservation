package org.example.voyage.booking;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.UUID;

public class BookingSpecification {
    public static Specification<Booking> byHotelId(UUID hotelId) {
        return (root, query, cb) ->
                cb.equal(root.get("room").get("hotel").get("id"), hotelId);
    }

    public static Specification<Booking> byStatus(Booking.BookingStatus status) {
        return (root, query, cb) ->
                cb.equal(root.get("status"), status);
    }

    public static Specification<Booking> checkInAfter(LocalDate from) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("checkInDate"), from);
    }

    public static Specification<Booking> checkInBefore(LocalDate to) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("checkInDate"), to);
    }
}
