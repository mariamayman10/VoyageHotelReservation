package org.example.voyage.hotel;

import jakarta.validation.Valid;
import org.example.voyage.hotel.dto.*;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {
    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping
    public ResponseEntity<List<HotelResponse>> getHotels(@Valid @ParameterObject @ModelAttribute SearchCriteria criteria) {
        return ResponseEntity.status(HttpStatus.OK).body(hotelService.getAllHotels(criteria));
    }
    @GetMapping("/{id}")
    public ResponseEntity<HotelResponse> getHotelById(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(hotelService.getHotelById(id));
    }
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<HotelDetailedResponse> create(@Valid @RequestBody CreateHotelRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.create(request, userDetails));
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<HotelDetailedResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateHotelRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(hotelService.update(id, request, userDetails));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        hotelService.delete(id, userDetails);
        return ResponseEntity.noContent().build();
    }
}
