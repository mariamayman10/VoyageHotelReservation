package org.example.voyage.hotel;

import org.example.voyage.amenity.AmenityMapper;
import org.example.voyage.hotel.dto.CreateHotelRequest;
import org.example.voyage.hotel.dto.HotelDetailedResponse;
import org.example.voyage.user.UserMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, AmenityMapper.class})
public interface HotelMapper {
    Hotel toEntity(CreateHotelRequest request);
    HotelDetailedResponse toHotelDetailedResponse(Hotel hotel);
}
