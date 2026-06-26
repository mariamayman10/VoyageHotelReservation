package org.example.voyage.hotel;

import org.example.voyage.amenity.Amenity;
import org.example.voyage.amenity.AmenityRepository;
import org.example.voyage.exception.HotelHasReservationsException;
import org.example.voyage.exception.NotAuthorizedException;
import org.example.voyage.exception.NotFoundException;
import org.example.voyage.hotel.dto.*;
import org.example.voyage.user.User;
import org.example.voyage.user.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import static org.example.voyage.hotel.HotelSpecification.*;


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
        Hotel hotel = checkHotelOwn(hotelId, userDetails);
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

    @Transactional
    public List<HotelResponse> getAllHotels(SearchCriteria criteria){
        List<Specification<Hotel>> specs = new ArrayList<>();
        if (criteria.getQuery() != null && !criteria.getQuery().isBlank())
            specs.add(byQuery(criteria.getQuery()));
        if (criteria.getCountry() != null && !criteria.getCountry().isBlank())
            specs.add(byCountry(criteria.getCountry()));
        if (criteria.getCity() != null && !criteria.getCity().isBlank())
            specs.add(byCity(criteria.getCity()));
        if (criteria.getAmenityIds() != null && !criteria.getAmenityIds().isEmpty())
            specs.add(byAmenityIds(criteria.getAmenityIds()));
        Sort sort = Sort.by(
                Sort.Direction.fromString(criteria.getSortDirection().name()),
                criteria.getSortBy()
        );
        PageRequest pageable = PageRequest.of(criteria.getPage(), criteria.getSize(), sort);
        Specification<Hotel> spec = Specification.allOf(specs);
        return hotelRepository.findAll(spec, pageable).map(hotelMapper::toHotelResponse).toList();
    }

    @Transactional
    public HotelResponse getHotelById(UUID id){
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Hotel not found"));
        return hotelMapper.toHotelResponse(hotel);
    }

    public Hotel getHotelEntityById(UUID id){
        return hotelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Hotel not found"));
    }

    public void delete(UUID hotelId, UserDetails userDetails) {
        Hotel hotel = checkHotelOwn(hotelId, userDetails);
        boolean hasReservations = hotelRepository.hasReservations(hotelId, LocalDate.now());
        if (hasReservations) {
            throw new HotelHasReservationsException("Hotel can't be deleted while it has active reservations");
        }
        hotelRepository.delete(hotel);
    }

    private Hotel checkHotelOwn(UUID hotelId, UserDetails userDetails) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new NotFoundException("Hotel not found"));
        User manager = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("Manager not found"));
        if(!hotel.getManager().getId().equals(manager.getId())){
            throw new NotAuthorizedException("You are not allowed to modify other managers' hotels");
        }
        return hotel;
    }

}
