package org.example.voyage.room;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.example.voyage.booking.Booking;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class RoomSpecification {
    public static Specification<Room> byHotelId(UUID hotelId) {
        return (root, q, cb) ->
                cb.equal(root.get("hotel").get("id"), hotelId);
    }
    public static Specification<Room> byRoomType(Room.RoomType type) {
        return (root, q, cb) ->
                cb.equal(root.get("type"), type);
    }

    public static Specification<Room> byRoomStatus(Room.RoomStatus status) {
        return (root, q, cb) ->
                cb.equal(root.get("status"), status);
    }

    public static Specification<Room> byCapacity(Integer capacity) {
        return (root, q, cb) ->
                cb.greaterThanOrEqualTo(root.get("capacity"), capacity);
    }

    public static Specification<Room> byCity(String city) {
        String query = "%" + city.toLowerCase() + "%";
        return (root, q, cb) ->
                cb.like(cb.lower(root.get("hotel").get("city")), query);
    }

    public static Specification<Room> byMinPrice(BigDecimal minPrice) {
        return (root, q, cb) ->
                cb.greaterThanOrEqualTo(root.get("pricePerNight"), minPrice);
    }

    public static Specification<Room> byMaxPrice(BigDecimal maxPrice) {
        return (root, q, cb) ->
                cb.lessThanOrEqualTo(root.get("pricePerNight"), maxPrice);
    }

    public static Specification<Room> isAvailableForDates(LocalDate checkInDate, LocalDate checkOutDate) {
        return (root, q, cb) -> {
            Subquery<Long> subquery = q.subquery(Long.class);
            Root<Booking> booking = subquery.from(Booking.class);

            subquery.select(cb.literal(1L))
                    .where(
                            cb.equal(booking.get("room"), root),
                            cb.lessThan(booking.get("checkInDate"), checkOutDate),
                            cb.greaterThan(booking.get("checkOutDate"), checkInDate),
                            cb.notEqual(booking.get("status"), Booking.BookingStatus.CANCELLED)
                    );

            return cb.and(
                    cb.not(cb.exists(subquery)),
                    cb.notEqual(root.get("status"), Room.RoomStatus.MAINTENANCE)
            );
        };
    }
}
