package org.example.voyage.amenity;

import org.example.voyage.amenity.dto.CreateAmenityRequest;
import org.example.voyage.amenity.dto.UpdateAmenityRequest;
import org.example.voyage.amenity.dto.AmenityResponse;
import org.example.voyage.exception.AmenityInUseException;
import org.example.voyage.exception.MissingPathVariableException;
import org.example.voyage.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AmenityService {
    private final AmenityRepository amenityRepository;
    private final AmenityMapper amenityMapper;

    public AmenityService(AmenityRepository amenityRepository, AmenityMapper amenityMapper) {
        this.amenityRepository = amenityRepository;
        this.amenityMapper = amenityMapper;
    }
    public AmenityResponse create(CreateAmenityRequest request) {
        Amenity amenity = amenityMapper.toEntity(request);
        amenity = amenityRepository.save(amenity);
        return amenityMapper.toAmenityResponse(amenity);
    }
    public AmenityResponse update(UpdateAmenityRequest request, UUID id) {
        if (id == null)
            throw new MissingPathVariableException("Amenity id is not provided to update");
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No amenity found with the given id"));

        if(request.getName() != null)
            amenity.setName(request.getName());
        if(request.getIcon() != null)
            amenity.setIcon(request.getIcon());

        amenityRepository.save(amenity);
        return amenityMapper.toAmenityResponse(amenity);
    }
    public AmenityResponse findById(UUID id) {
        if (id == null)
            throw new MissingPathVariableException("Amenity id is not provided to retrieve");
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No amenity found with the given id"));
        return amenityMapper.toAmenityResponse(amenity);
    }
    public void delete(UUID id) {
        if (id == null)
            throw new MissingPathVariableException("Amenity id is not provided to delete");
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No amenity found with the given id"));
        if(amenityRepository.existsByIdAndHotelsIsNotEmpty(amenity.getId()))
            throw new AmenityInUseException("Can't delete amenity assigned to hotels");
        amenityRepository.delete(amenity);
    }
}
