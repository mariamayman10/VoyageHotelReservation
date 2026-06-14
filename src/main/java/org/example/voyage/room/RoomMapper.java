package org.example.voyage.room;

import org.example.voyage.room.dto.CreateRoomRequest;
import org.example.voyage.room.dto.RoomDetailedResponse;
import org.example.voyage.room.dto.RoomResponse;
import org.example.voyage.room.dto.UpdateRoomRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    @Mapping(target = "hotel", ignore = true)
    Room toEntity(CreateRoomRequest createRoomRequest);
    @Mapping(target = "hotelName", source = "hotel.name")
    RoomDetailedResponse toDetailedResponse(Room room);
    @Mapping(target = "hotelName", source = "hotel.name")
    RoomResponse toResponse(Room room);
    @BeanMapping(nullValuePropertyMappingStrategy =  NullValuePropertyMappingStrategy.IGNORE)
    void updateRoom(UpdateRoomRequest request, @MappingTarget Room room);
}
