package org.example.voyage.hotel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, UUID>, JpaSpecificationExecutor<Hotel> {
    @Query("""
            SELECT COUNT(b) > 0 FROM Booking b
            WHERE b.room.hotel.id = :hotelId
                  AND b.status = 'CONFIRMED'
                  AND b.checkOutDate >= :today
            """)
    boolean hasReservations(@Param("hotelId") UUID hotelId, @Param("today") LocalDate today);
}
