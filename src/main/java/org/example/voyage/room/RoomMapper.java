package org.example.voyage.room;

import org.example.voyage.room.dto.CreateRoomRequest;
import org.example.voyage.room.dto.RoomResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    @Mapping(target = "hotel", ignore = true)
    Room toEntity(CreateRoomRequest createRoomRequest);
    RoomResponse toResponse(Room room);
}
