package org.example.voyage.room;

import jakarta.validation.Valid;
import org.example.voyage.room.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<RoomDetailedResponse> create(@Valid @RequestBody CreateRoomRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.create(request, userDetails));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<RoomDetailedResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateRoomRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(roomService.update(id, request, userDetails));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        roomService.delete(id, userDetails);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(roomService.findById(id));
    }
    @GetMapping("/available")
    public ResponseEntity<List<RoomResponse>> getAllAvailableRooms(
            @RequestParam(required = false) UUID hotelId,
            @Valid PublicRoomSearchCriteria criteria) {
        return ResponseEntity.ok(roomService.findAllAvailableRooms(hotelId, criteria));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<RoomDetailedResponse>> getManagerRooms(
            @RequestParam(required = false) UUID hotelId,
            @Valid ManagerRoomSearchCriteria criteria,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(roomService.findAllRooms(hotelId, criteria, userDetails));
    }
}
