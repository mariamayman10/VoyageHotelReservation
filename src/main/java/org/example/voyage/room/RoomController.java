package org.example.voyage.room;

import jakarta.validation.Valid;
import org.example.voyage.room.dto.CreateRoomRequest;
import org.example.voyage.room.dto.RoomResponse;
import org.example.voyage.room.dto.UpdateRoomRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/room")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<RoomResponse> create(@Valid @RequestBody CreateRoomRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.create(request, userDetails));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<RoomResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateRoomRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(roomService.update(id, request, userDetails));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<RoomResponse> delete(@PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        roomService.delete(id, userDetails);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(roomService.findById(id));
    }
}
