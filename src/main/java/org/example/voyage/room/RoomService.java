package org.example.voyage.room;

import jakarta.transaction.Transactional;
import org.example.voyage.exception.NotAuthorizedException;
import org.example.voyage.exception.NotFoundException;
import org.example.voyage.hotel.Hotel;
import org.example.voyage.hotel.HotelRepository;
import org.example.voyage.hotel.dto.UpdateHotelRequest;
import org.example.voyage.room.dto.CreateRoomRequest;
import org.example.voyage.room.dto.RoomResponse;
import org.example.voyage.room.dto.UpdateRoomRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final RoomMapper roomMapper;

    public RoomService(RoomRepository roomRepository, HotelRepository hotelRepository, RoomMapper roomMapper) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
        this.roomMapper = roomMapper;
    }

    @Transactional
    public RoomResponse create(CreateRoomRequest request, UserDetails userDetails) {
        Hotel hotel = checkHotelOwnership(request.getHotel(), userDetails);
        Room room = roomMapper.toEntity(request);
        room.setHotel(hotel);
        return roomMapper.toResponse(roomRepository.save(room));
    }

    @Transactional
    public RoomResponse update(UUID id, UpdateRoomRequest request, UserDetails userDetails) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room not found"));
        if(!room.getHotel().getManager().getEmail().equals(userDetails.getUsername())){
            throw new NotAuthorizedException("You can't update a room in another manager's hotel");
        }
        roomMapper.updateRoom(request, room);
        return roomMapper.toResponse(roomRepository.save(room));
    }
    private Hotel checkHotelOwnership(UUID hotelId, UserDetails userDetails) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new NotFoundException("Hotel not found"));
        if(!hotel.getManager().getEmail().equals(userDetails.getUsername())){
            throw new NotAuthorizedException("You can't create a room in another manager's hotel");
        }
        return hotel;
    }
}
