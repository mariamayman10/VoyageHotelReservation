package org.example.voyage.amenity;

import org.example.voyage.amenity.dto.CreateAmenityRequest;
import org.example.voyage.amenity.dto.UpdateAmenityRequest;
import org.example.voyage.amenity.dto.AmenityResponse;
import org.example.voyage.exception.AmenityInUseException;
import org.example.voyage.exception.MissingPathVariableException;
import org.example.voyage.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AmenityService {
    private final AmenityRepository amenityRepository;
    public AmenityService(AmenityRepository amenityRepository) {
        this.amenityRepository = amenityRepository;
    }
    public AmenityResponse create(CreateAmenityRequest request) {
        Amenity amenity = new Amenity();
        amenity.setName(request.getName());
        amenity.setIcon(request.getIcon());
        amenity = amenityRepository.save(amenity);

        return new AmenityResponse(amenity.getId(), amenity.getName(), amenity.getIcon());
    }
    public AmenityResponse update(UpdateAmenityRequest request, UUID id) {
        if (id == null) {
            throw new MissingPathVariableException("Amenity id is not provided to update");
        }
        Optional<Amenity> amenity = amenityRepository.findById(id);
        if(amenity.isEmpty()) {
            throw new NotFoundException("No amenity found with the given id");
        }
        if(request.getName() != null)
            amenity.get().setName(request.getName());
        if(request.getIcon() != null)
            amenity.get().setIcon(request.getIcon());
        amenityRepository.save(amenity.get());
        return new AmenityResponse(amenity.get().getId(), amenity.get().getName(), amenity.get().getIcon());
    }
    public AmenityResponse findById(UUID id) {
        if (id == null) {
            throw new MissingPathVariableException("Amenity id is not provided to retrieve");
        }
        Optional<Amenity> amenity = amenityRepository.findById(id);
        if(amenity.isEmpty()) {
            throw new NotFoundException("No amenity found with the given id");
        }
        return new AmenityResponse(amenity.get().getId(), amenity.get().getName(), amenity.get().getIcon());
    }
    public void delete(UUID id) {
        if (id == null) {
            throw new MissingPathVariableException("Amenity id is not provided to delete");
        }
        Optional<Amenity> amenity = amenityRepository.findById(id);
        if(amenity.isEmpty()) {
            throw new NotFoundException("No amenity found with the given id");
        }
        if(amenityRepository.existsByIdAndHotelsIsNotEmpty(amenity.get().getId())) {
            throw new AmenityInUseException("Can't delete amenity assigned to hotels");
        }
        amenityRepository.delete(amenity.get());
    }
}
