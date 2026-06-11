package org.example.voyage.hotel.dto;

import lombok.*;
import org.example.voyage.amenity.dto.AmenityResponse;
import org.example.voyage.user.dto.ManagerSummaryResponse;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelDetailedResponse {
    private UUID id;
    private String name;
    private String description;
    private String country;
    private String city;
    private String address;
    private String contactPhone;
    private String contactEmail;
    private ManagerSummaryResponse manager;
    private Set<AmenityResponse> amenities;
}
