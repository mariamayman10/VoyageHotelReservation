package org.example.voyage.booking;

import org.example.voyage.booking.dto.BookingRequest;
import org.example.voyage.booking.dto.BookingResponse;
import org.example.voyage.room.RoomMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring",  uses = RoomMapper.class)
public interface BookingMapper {
    @Mappings({
            @Mapping(target = "room", ignore = true)
    })
    Booking toEntity(BookingRequest bookingRequest);

    @Mapping(target = "status", source = "status")
    BookingResponse toResponse(Booking booking);
}
