package org.example.voyage.hotel;

import org.example.voyage.amenity.AmenityMapper;
import org.example.voyage.hotel.dto.CreateHotelRequest;
import org.example.voyage.hotel.dto.HotelDetailedResponse;
import org.example.voyage.hotel.dto.UpdateHotelRequest;
import org.example.voyage.user.UserMapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = {UserMapper.class, AmenityMapper.class})
public interface HotelMapper {
    Hotel toEntity(CreateHotelRequest request);
    HotelDetailedResponse toHotelDetailedResponse(Hotel hotel);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateHotel(UpdateHotelRequest request, @MappingTarget Hotel hotel);
}
