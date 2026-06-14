package org.example.voyage.room;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID>, JpaSpecificationExecutor<Room> {
    @EntityGraph(attributePaths = "hotel")
    @Query("SELECT r FROM Room r WHERE r.id = :id")
    Optional<Room> findByIdWithHotel(UUID id);
    @EntityGraph(attributePaths = "hotel")
    Page<Room> findAll(@NonNull Specification<Room> spec, @NonNull Pageable pageable);
}
