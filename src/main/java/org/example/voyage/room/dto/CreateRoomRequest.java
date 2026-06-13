package org.example.voyage.room.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.example.voyage.room.Room;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRoomRequest {
    @NotBlank(message = "Room number cannot be blank")
    @Size(min = 1, max = 5, message = "Room number's length must be between 1 and 5")
    private String number;
    @Min(value = 1, message = "Floor must be 1 at least")
    private int floor;
    @Min(value = 1, message = "Capacity must be 1 at least")
    private int capacity;
    @Positive(message = "Price of room per night must be positive")
    @NotNull(message = "Price of room per night can't be null")
    private BigDecimal pricePerNight;
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @NotBlank(message = "Room description can't be blank")
    private String description;
    @NotNull(message = "Room type can't be null")
    private Room.RoomType type = Room.RoomType.SINGLE;
    @NotNull(message = "Room status can't be null")
    private Room.RoomStatus status = Room.RoomStatus.AVAILABLE;
    @NotNull(message = "Hotel id must be defined")
    private UUID hotel;
}
