package org.example.voyage.amenity.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAmenityRequest {
    @Pattern(regexp = "^(?!\\s*$).+", message = "Amenity name mustn't be blank")
    private String name;
    @Pattern(regexp = "^(?!\\s*$).+", message = "Amenity icon mustn't be blank")
    private String icon;
}
