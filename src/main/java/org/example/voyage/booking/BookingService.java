package org.example.voyage.booking;

import org.example.voyage.booking.dto.BookingRequest;
import org.example.voyage.booking.dto.BookingResponse;
import org.example.voyage.booking.dto.Pagination;
import org.example.voyage.exception.NotAuthorizedException;
import org.example.voyage.exception.NotFoundException;
import org.example.voyage.exception.OperationCanNotBeCompleted;
import org.example.voyage.exception.RoomInUseException;
import org.example.voyage.room.Room;
import org.example.voyage.room.RoomService;
import org.example.voyage.user.User;
import org.example.voyage.user.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserService userService;
    private final RoomService roomService;

    public BookingService(BookingRepository bookingRepository, BookingMapper bookingMapper, UserService userService, RoomService roomService) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.userService = userService;
        this.roomService = roomService;
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
        return bookingMapper.toListResponse(bookings);
    }

    @Transactional
    public BookingResponse getMyBookingById(UUID id, UserDetails userDetails) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        if(booking.getUser().getEmail().equals(userDetails.getUsername()))
            return bookingMapper.toResponse(booking);
        else throw new NotAuthorizedException("You are not allowed to cancel another customer's booking");
    }
}
