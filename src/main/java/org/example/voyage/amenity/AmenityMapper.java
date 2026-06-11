package org.example.voyage.amenity;

import org.example.voyage.amenity.dto.AmenityResponse;
import org.example.voyage.amenity.dto.CreateAmenityRequest;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface AmenityMapper {
    Amenity toEntity(CreateAmenityRequest request);
    AmenityResponse toAmenityResponse(Amenity amenity);
    Set<AmenityResponse> toResponseSet(Set<Amenity> amenities);
}
