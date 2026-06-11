package org.example.voyage.hotel;

import org.example.voyage.amenity.Amenity;
import org.example.voyage.amenity.AmenityRepository;
import org.example.voyage.exception.NotFoundException;
import org.example.voyage.hotel.dto.CreateHotelRequest;
import org.example.voyage.hotel.dto.HotelDetailedResponse;
import org.example.voyage.user.User;
import org.example.voyage.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

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

        if (request.getAmenityIds() != null && !request.getAmenityIds().isEmpty()) {
            Set<Amenity> amenities = new HashSet<>(amenityRepository.findAllById(request.getAmenityIds()));
            if (amenities.size() != request.getAmenityIds().size())
                throw new NotFoundException("One or more amenities not found");
            hotel.setAmenities(amenities);
        }

        hotel = hotelRepository.save(hotel);
        return hotelMapper.toHotelDetailedResponse(hotel);
    }
}
