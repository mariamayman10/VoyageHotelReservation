package org.example.voyage.amenity.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmenityResponse {
    private UUID id;
    private String name;
    private String icon;
}
