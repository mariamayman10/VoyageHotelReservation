package org.example.voyage.hotel;

import org.example.voyage.amenity.AmenityMapper;
import org.example.voyage.hotel.dto.CreateHotelRequest;
import org.example.voyage.hotel.dto.HotelDetailedResponse;
import org.example.voyage.hotel.dto.HotelResponse;
import org.example.voyage.hotel.dto.UpdateHotelRequest;
import org.example.voyage.user.UserMapper;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, AmenityMapper.class})
public interface HotelMapper {
    @Mapping(target = "amenities", ignore = true)
    Hotel toEntity(CreateHotelRequest request);
    HotelDetailedResponse toHotelDetailedResponse(Hotel hotel);
    List<HotelDetailedResponse> toHotelDetailedResponseList(List<Hotel> hotel);
    HotelResponse toHotelResponse(Hotel hotel);
    @Mapping(target = "amenities", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateHotel(UpdateHotelRequest request, @MappingTarget Hotel hotel);
}
