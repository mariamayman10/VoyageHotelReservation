package org.example.voyage.room;

import jakarta.validation.Valid;
import org.example.voyage.room.dto.ManagerRoomSearchCriteria;
import org.example.voyage.room.dto.PublicRoomSearchCriteria;
import org.example.voyage.room.dto.RoomDetailedResponse;
import org.example.voyage.room.dto.RoomResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/hotels")
public class HotelRoomController {
    private final RoomService roomService;

    public HotelRoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/rooms")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<RoomDetailedResponse>> getAllRooms(@Valid ManagerRoomSearchCriteria criteria) {
        return ResponseEntity.status(HttpStatus.OK).body(roomService.findAllRooms(criteria));
    }
    @GetMapping("/{hotelId}/rooms")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<RoomDetailedResponse>> getAllRoomsOfHotel(@PathVariable UUID hotelId, @Valid ManagerRoomSearchCriteria criteria) {
        return ResponseEntity.status(HttpStatus.OK).body(roomService.findAllRoomsOfHotel(hotelId, criteria));
    }
    @GetMapping("/{hotelId}/rooms/available")
    public ResponseEntity<List<RoomResponse>> getAllAvailableRoomsOfHotel(@PathVariable UUID hotelId, @Valid PublicRoomSearchCriteria criteria) {
        return ResponseEntity.status(HttpStatus.OK).body(roomService.findAllAvailableRoomsOfHotel(hotelId, criteria));
    }
}
