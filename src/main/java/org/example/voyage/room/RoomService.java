package org.example.voyage.room;

import jakarta.transaction.Transactional;
import org.example.voyage.exception.NotAuthorizedException;
import org.example.voyage.exception.NotFoundException;
import org.example.voyage.exception.RoomInUseException;
import org.example.voyage.hotel.Hotel;
import org.example.voyage.hotel.HotelRepository;
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
        Room room = checkRoomOwnership(id, userDetails);
        roomMapper.updateRoom(request, room);
        return roomMapper.toResponse(roomRepository.save(room));
    }

    @Transactional
    public void delete(UUID id, UserDetails userDetails) {
        Room room = checkRoomOwnership(id, userDetails);
        if(room.getStatus().equals(Room.RoomStatus.BOOKED)){
            throw new RoomInUseException("Can't delete the room because it is already booked");
        }
        roomRepository.delete(room);
    }

    public RoomResponse findById(UUID id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room not found"));
        return roomMapper.toResponse(room);
    }

    private Hotel checkHotelOwnership(UUID hotelId, UserDetails userDetails) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new NotFoundException("Hotel not found"));
        if(!hotel.getManager().getEmail().equals(userDetails.getUsername())){
            throw new NotAuthorizedException("You can't create a room in another manager's hotel");
        }
        return hotel;
    }

    private Room checkRoomOwnership(UUID roomId, UserDetails userDetails) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room not found"));
        if(!room.getHotel().getManager().getEmail().equals(userDetails.getUsername())){
            throw new NotAuthorizedException("You can't access a room in another manager's hotel");
        }
        return room;
    }
}
