package org.example.voyage.room.dto;

import lombok.*;
import org.example.voyage.room.Room;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomResponse {
    private UUID id;
    private String number;
    private int floor;
    private int capacity;
    private BigDecimal pricePerNight;
    private String description;
    private Room.RoomType type;
}
