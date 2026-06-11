package org.example.voyage.hotel.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.voyage.amenity.dto.AmenityResponse;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelResponse {
    private UUID id;
    private String name;
    private String description;
    private String country;
    private String city;
    private String address;
    private String contactPhone;
    private String contactEmail;
    private Set<AmenityResponse> amenities;
}
