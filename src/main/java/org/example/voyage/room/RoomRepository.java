package org.example.voyage.room;

import jakarta.persistence.LockModeType;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID>, JpaSpecificationExecutor<Room> {
    @EntityGraph(attributePaths = "hotel")
    @Query("SELECT r FROM Room r WHERE r.id = :id")
    Optional<Room> findByIdWithHotel(UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = "hotel")
    @Query("SELECT r FROM Room r WHERE r.id = :id")
    Optional<Room> findByIdWithLock(UUID id);

    @EntityGraph(attributePaths = "hotel")
    Page<Room> findAll(@NonNull Specification<Room> spec, @NonNull Pageable pageable);

    @Query("""
        SELECT COUNT(b) > 0 FROM Booking b
        WHERE b.room.id = :roomId
        AND b.status NOT IN ('CANCELLED')
        AND b.checkOutDate > :today
    """)
    boolean hasActiveOrFutureBookings(@Param("roomId") UUID roomId, @Param("today") LocalDate today);
}
