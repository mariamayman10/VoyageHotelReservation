package org.example.voyage.hotel;

import org.example.voyage.amenity.Amenity;
import org.example.voyage.amenity.AmenityRepository;
import org.example.voyage.exception.NotAuthorizedException;
import org.example.voyage.exception.NotFoundException;
import org.example.voyage.hotel.dto.CreateHotelRequest;
import org.example.voyage.hotel.dto.HotelDetailedResponse;
import org.example.voyage.hotel.dto.UpdateHotelRequest;
import org.example.voyage.user.User;
import org.example.voyage.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class HotelService {
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;
    private final AmenityRepository amenityRepository;
    private final HotelMapper hotelMapper;

    public HotelService(HotelRepository hotelRepository, UserRepository userRepository, AmenityRepository amenityRepository, HotelMapper hotelMapper) {
        this.hotelRepository = hotelRepository;
        this.userRepository = userRepository;
        this.amenityRepository = amenityRepository;
        this.hotelMapper = hotelMapper;
    }

    public HotelDetailedResponse create(CreateHotelRequest request, UserDetails userDetails) {
        User manager = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("Manager not found"));
        Hotel hotel = hotelMapper.toEntity(request);
        hotel.setManager(manager);

        if (request.getAmenities() != null && !request.getAmenities().isEmpty()) {
            Set<Amenity> amenities = new HashSet<>(amenityRepository.findAllById(request.getAmenities()));
            if (amenities.size() != request.getAmenities().size())
                throw new NotFoundException("One or more amenities not found");
            hotel.setAmenities(amenities);
        }

        hotel = hotelRepository.save(hotel);
        return hotelMapper.toHotelDetailedResponse(hotel);
    }

    @Transactional
    public HotelDetailedResponse update(UUID hotelId, UpdateHotelRequest request, UserDetails userDetails) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new NotFoundException("Hotel not found"));
        User manager = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("Manager not found"));
        if(!hotel.getManager().getId().equals(manager.getId())){
            throw new NotAuthorizedException("You are not allowed to modify other managers' hotels");
        }
        hotelMapper.updateHotel(request, hotel);
        if (request.getAmenities() != null){
            if (request.getAmenities().isEmpty()) {
                hotel.setAmenities(new HashSet<>());
            } else {
                Set<Amenity> amenities = new HashSet<>(amenityRepository.findAllById(request.getAmenities()));
                if (amenities.size() != request.getAmenities().size())
                    throw new NotFoundException("One or more amenities not found");
                hotel.setAmenities(amenities);
            }
        }
        hotel = hotelRepository.save(hotel);
        return hotelMapper.toHotelDetailedResponse(hotel);
    }
}
