package org.example.voyage.room.dto;

import lombok.*;
import org.example.voyage.room.Room;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerRoomSearchCriteria {
    private Room.RoomStatus status;
    private Room.RoomType type;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private int page = 0;
    private int size = 10;
}
