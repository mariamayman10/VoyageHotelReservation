package org.example.voyage.room.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.example.voyage.room.Room;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRoomRequest {
    @Pattern(regexp = "^(?!\\s*$).+", message = "Room number can't be blank")
    @Size(max = 5, message = "Room number's length must be at most 5")
    private String number;
    @Min(value = 1, message = "Floor must be at least 1")
    private Integer floor;
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;
    @Positive(message = "Price per night must be positive")
    private BigDecimal pricePerNight;
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    private Room.RoomType type;
    private Room.RoomStatus status;
}
