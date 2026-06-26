package org.example.voyage.room;

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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
        if(roomRepository.hasActiveOrFutureBookings(id, LocalDate.now())){
            throw new RoomInUseException("Can't delete the room because it is already booked");
        }
        roomRepository.delete(room);
    }

    public RoomResponse findById(UUID id) {
        Room room = roomRepository.findByIdWithHotel(id)
                .orElseThrow(() -> new NotFoundException("Room not found"));
        return roomMapper.toResponse(room);
    }

    public Room findRoomEntityByIdWithLock(UUID id) {
        return roomRepository.findByIdWithLock(id)
                .orElseThrow(() -> new NotFoundException("Room not found"));
    }

    public List<RoomResponse> findAllAvailableRooms(UUID hotelId, PublicRoomSearchCriteria criteria) {
        Specification<Room> spec;
        if(hotelId != null){
            if(!hotelRepository.existsById(hotelId))
                throw new NotFoundException("Hotel not found");
            spec = buildPublicSpecs(criteria).and(byHotelId(hotelId));
        }
        else spec = buildPublicSpecs(criteria);
        PageRequest pageable = PageRequest.of(criteria.getPage(), criteria.getSize());
        return roomRepository.findAll(spec, pageable).map(roomMapper::toResponse).toList();
    }

    public List<RoomDetailedResponse> findAllRooms(UUID hotelId, ManagerRoomSearchCriteria criteria, UserDetails userDetails) {
        Specification<Room> spec;
        if(hotelId != null){
            if(!hotelRepository.existsById(hotelId))
                throw new NotFoundException("Hotel not found");
            spec = buildManagerSpecs(criteria, userDetails.getUsername()).and(byHotelId(hotelId));
        }
        else spec = buildManagerSpecs(criteria, userDetails.getUsername());
        PageRequest pageable = PageRequest.of(criteria.getPage(), criteria.getSize());
        return roomRepository.findAll(spec, pageable).map(roomMapper::toDetailedResponse).toList();
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

    private Specification<Room> buildPublicSpecs(PublicRoomSearchCriteria criteria) {
        List<Specification<Room>> specs = new ArrayList<>();
        specs.add(isAvailableForDates(criteria.getCheckInDate(), criteria.getCheckOutDate()));
        specs.add(byCapacity(criteria.getCapacity()));
        if (criteria.getCity() != null && !criteria.getCity().isBlank())
            specs.add(byCity(criteria.getCity()));
        if (criteria.getType() != null)
            specs.add(byRoomType(criteria.getType()));
        if (criteria.getMinPrice() != null)
            specs.add(byMinPrice(criteria.getMinPrice()));
        if (criteria.getMaxPrice() != null)
            specs.add(byMaxPrice(criteria.getMaxPrice()));
        return Specification.allOf(specs);
    }

    private Specification<Room> buildManagerSpecs(ManagerRoomSearchCriteria criteria, String username) {
        List<Specification<Room>> specs = new ArrayList<>();
        specs.add(byManagerId(username));
        if (criteria.getType() != null)
            specs.add(byRoomType(criteria.getType()));
        if (criteria.getStatus() != null)
            specs.add(byRoomStatus(criteria.getStatus()));
        if (criteria.getMinPrice() != null)
            specs.add(byMinPrice(criteria.getMinPrice()));
        if (criteria.getMaxPrice() != null)
            specs.add(byMaxPrice(criteria.getMaxPrice()));
        return Specification.allOf(specs);
    }
}
