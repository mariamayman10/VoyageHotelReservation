package org.example.voyage.room;

import jakarta.transaction.Transactional;
import org.example.voyage.exception.NotAuthorizedException;
import org.example.voyage.exception.NotFoundException;
import org.example.voyage.hotel.Hotel;
import org.example.voyage.hotel.HotelRepository;
import org.example.voyage.room.dto.CreateRoomRequest;
import org.example.voyage.room.dto.RoomResponse;
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
    public RoomResponse create(CreateRoomRequest createRoomRequest, UserDetails userDetails) {
        Hotel hotel = checkHotelOwnership(createRoomRequest.getHotel(), userDetails);
        Room room = roomMapper.toEntity(createRoomRequest);
        room.setHotel(hotel);
        return roomMapper.toResponse(roomRepository.save(room));
    }
    private Hotel checkHotelOwnership(UUID hotelId, UserDetails userDetails) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new NotFoundException("Hotel not found"));
        if(!hotel.getManager().getEmail().equals(userDetails.getUsername())){
            throw new NotAuthorizedException("You can't create a room of another manager's hotel");
        }
        return hotel;
    }
}
