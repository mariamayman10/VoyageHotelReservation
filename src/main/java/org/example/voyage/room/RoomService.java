package org.example.voyage.room;

import jakarta.transaction.Transactional;
import org.example.voyage.exception.NotAuthorizedException;
import org.example.voyage.exception.NotFoundException;
import org.example.voyage.exception.RoomInUseException;
import org.example.voyage.hotel.Hotel;
import org.example.voyage.hotel.HotelRepository;
import org.example.voyage.room.dto.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.example.voyage.room.RoomSpecification.*;

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
    public RoomDetailedResponse create(CreateRoomRequest request, UserDetails userDetails) {
        Hotel hotel = checkHotelOwnership(request.getHotel(), userDetails);
        Room room = roomMapper.toEntity(request);
        room.setHotel(hotel);
        return roomMapper.toDetailedResponse(roomRepository.save(room));
    }

    @Transactional
    public RoomDetailedResponse update(UUID id, UpdateRoomRequest request, UserDetails userDetails) {
        Room room = checkRoomOwnership(id, userDetails);
        roomMapper.updateRoom(request, room);
        return roomMapper.toDetailedResponse(roomRepository.save(room));
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

    public List<RoomResponse> findAllRooms(PublicRoomSearchCriteria criteria) {
        List<Specification<Room>> specs = new ArrayList<>();
        specs.add(isAvailableForDates(criteria.getCheckInDate(), criteria.getCheckOutDate()));
        specs.add(byCapacity(criteria.getCapacity()));
        if(criteria.getCity() != null && !criteria.getCity().isBlank())
            specs.add(byCity(criteria.getCity()));
        if(criteria.getType() != null && !criteria.getType().name().isBlank())
            specs.add(byRoomType(criteria.getType()));
        if(criteria.getMinPrice() != null)
            specs.add(byMinPrice(criteria.getMinPrice()));
        if(criteria.getMaxPrice() != null)
            specs.add(byMaxPrice(criteria.getMaxPrice()));

        PageRequest pageable = PageRequest.of(criteria.getPage(), criteria.getSize());
        Specification<Room> spec = Specification.allOf(specs);
        return roomRepository.findAll(spec, pageable).map(roomMapper::toResponse).toList();
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
