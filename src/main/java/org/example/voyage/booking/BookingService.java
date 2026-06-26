package org.example.voyage.booking;

import org.example.voyage.booking.dto.*;
import org.example.voyage.exception.NotAuthorizedException;
import org.example.voyage.exception.NotFoundException;
import org.example.voyage.exception.OperationCanNotBeCompleted;
import org.example.voyage.exception.RoomInUseException;
import org.example.voyage.hotel.Hotel;
import org.example.voyage.hotel.HotelService;
import org.example.voyage.room.Room;
import org.example.voyage.room.RoomService;
import org.example.voyage.user.User;
import org.example.voyage.user.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.example.voyage.booking.BookingSpecification.*;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserService userService;
    private final RoomService roomService;
    private final HotelService hotelService;

    public BookingService(BookingRepository bookingRepository, BookingMapper bookingMapper, UserService userService, RoomService roomService, HotelService hotelService) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.userService = userService;
        this.roomService = roomService;
        this.hotelService = hotelService;
    }

    @Transactional
    public BookingResponse create(BookingRequest request, UserDetails userDetails) {
        Room room = roomService.findRoomEntityByIdWithLock(request.getRoomId());
        boolean isOverlapped = bookingRepository.existsOverlappingBooking(request.getRoomId(), request.getCheckInDate(), request.getCheckOutDate());
        if (isOverlapped) {
            throw new RoomInUseException("The room has been booked already");
        }
        Booking booking = bookingMapper.toEntity(request);
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));
        booking.setUser(user);
        booking.setRoom(room);
        BigDecimal days = new BigDecimal(ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate()));
        booking.setTotalPrice(room.getPricePerNight().multiply(days));
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        return bookingMapper.toResponse(bookingRepository.save(booking));
    }

    @Transactional
    public void cancel(UUID id, UserDetails userDetails) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        if (!booking.getUser().getEmail().equals(userDetails.getUsername()))
            throw new NotAuthorizedException("You are not allowed to cancel another user's booking");
        if(booking.getStatus().equals(Booking.BookingStatus.CANCELLED))
            throw new OperationCanNotBeCompleted("Booking is already cancelled");
        if (booking.getCheckInDate().isAfter(LocalDate.now()))
            bookingRepository.delete(booking);
        else throw new OperationCanNotBeCompleted("You can't cancel booking with a passed check-in date");
    }

    @Transactional
    public List<BookingResponse> getMyBookings(Pagination pagination, UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));
        PageRequest pageable = PageRequest.of(pagination.getPage(), pagination.getSize());
        List<Booking> bookings = bookingRepository.findAllByUser_Id(user.getId(), pageable);
        return bookings.stream().map(bookingMapper::toResponse).toList();
    }

    @Transactional
    public BookingResponse getMyBookingById(UUID id, UserDetails userDetails) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        if(booking.getUser().getEmail().equals(userDetails.getUsername()))
            return bookingMapper.toResponse(booking);
        else throw new NotAuthorizedException("You are not allowed to cancel another customer's booking");
    }

    @Transactional
    public List<ManagerBookingResponse> getHotelBookings(BookingSearchCriteria criteria, UUID hotelId, UserDetails userDetails) {
        Hotel hotel = hotelService.getHotelEntityById(hotelId);
        if(!hotel.getManager().getEmail().equals(userDetails.getUsername()))
            throw new NotAuthorizedException("You are not allowed to access another hotel's bookings");

        Specification<Booking> spec = Specification.where(byHotelId(hotelId));
        if (criteria.getStatus() != null)
            spec = spec.and(byStatus(criteria.getStatus()));
        if (criteria.getFrom() != null)
            spec = spec.and(checkInAfter(criteria.getFrom()));
        if (criteria.getTo() != null)
            spec = spec.and(checkInBefore(criteria.getTo()));

        PageRequest pageable = PageRequest.of(criteria.getPage(), criteria.getSize());
        return bookingRepository.findAll(spec, pageable).map(bookingMapper::toManagerBookingResponse).toList();
    }
}
