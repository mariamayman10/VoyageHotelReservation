package org.example.voyage.amenity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, UUID> {
    boolean existsByIdAndHotelsIsNotEmpty(UUID id);
}
