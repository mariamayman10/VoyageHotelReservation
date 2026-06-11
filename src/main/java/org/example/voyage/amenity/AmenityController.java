package org.example.voyage.amenity;

import jakarta.validation.Valid;
import org.example.voyage.amenity.dto.CreateAmenityRequest;
import org.example.voyage.amenity.dto.UpdateAmenityRequest;
import org.example.voyage.amenity.dto.AmenityResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/amenity")
public class AmenityController {
    AmenityService amenityService;

    public AmenityController(AmenityService amenityService) {
        this.amenityService = amenityService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AmenityResponse> create(@Valid @RequestBody CreateAmenityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(amenityService.create(request));
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AmenityResponse> update(@Valid @RequestBody UpdateAmenityRequest request, @PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(amenityService.update(request, id));
    }
    @GetMapping("/{id}")
    public ResponseEntity<AmenityResponse> get(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(amenityService.findById(id));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        amenityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
