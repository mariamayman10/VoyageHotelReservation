package org.example.voyage.amenity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAmenityRequest {
    @NotBlank(message = "Name is required for amenity")
    private String name;
    @NotBlank(message = "Icon is required for amenity")
    private String icon;
}
