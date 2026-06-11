package org.example.voyage.hotel.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateHotelRequest {
    @Pattern(regexp = "^(?!\\s*$).+", message = "Hotel name can't be blank")
    @Size(max = 150)
    private String name;
    private String description;
    @Pattern(regexp = "^(?!\\s*$).+", message = "Country can't be blank")
    @Size(max = 100)
    private String country;
    @Pattern(regexp = "^(?!\\s*$).+", message = "City can't be blank")
    @Size(max = 100)
    private String city;
    @Pattern(regexp = "^(?!\\s*$).+", message = "Address can't be blank")
    @Size(max = 100)
    private String address;
    @Pattern(
            regexp = "^\\+?[0-9]{7,15}$",
            message = "Contact phone is invalid, it can contain digits only " +
                    "- optional + at the beginning for country code - min 7, and max 15 digits")
    private String contactPhone;
    @Pattern(regexp = "^(?!\\s*$).+", message = "Contact email can't be blank")
    @Email(message = "Invalid contact email")
    private String contactEmail;
    private Set<UUID> amenityIds;
}
