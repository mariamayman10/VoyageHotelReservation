package org.example.voyage.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    @Query("""
                SELECT COUNT(b) > 0 FROM Booking b
                WHERE b.room.id = :roomId
                  AND b.status <> 'CANCELLED'
                  AND b.checkInDate  < :checkOutDate
                  AND b.checkOutDate > :checkInDate
            """)
    boolean existsOverlappingBooking(
            UUID roomId,
            LocalDate checkInDate,
            LocalDate checkOutDate
    );
}
