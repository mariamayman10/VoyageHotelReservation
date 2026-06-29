package org.example.voyage.payment;

import org.example.voyage.booking.Booking;
import org.example.voyage.booking.BookingRepository;
import org.example.voyage.exception.NotAuthorizedException;
import org.example.voyage.exception.NotFoundException;
import org.example.voyage.exception.OperationCanNotBeCompleted;
import org.example.voyage.payment.dto.PaymentResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final BookingRepository bookingRepository;

    public PaymentService(PaymentRepository paymentRepository, PaymentMapper paymentMapper, BookingRepository bookingRepository) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public PaymentResponse pay(UUID bookingId, UserDetails userDetails) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        if(!booking.getUser().getEmail().equals(userDetails.getUsername()))
            throw new NotAuthorizedException("You can't pay another customer's booking");
        if(booking.getStatus().name().equals(Booking.BookingStatus.CANCELLED.name()))
            throw new NotAuthorizedException("Can't pay a cancelled booking");
        Payment payment = paymentRepository.findByBookingId(bookingId);
        if(payment != null && payment.getStatus() == Payment.PaymentStatus.COMPLETED)
            throw new OperationCanNotBeCompleted("Payment is already completed");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalPrice());
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        payment.setPaidAt(LocalDateTime.now());

        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        paymentRepository.save(payment);

        return paymentMapper.toPaymentResponse(payment);
    }

    @Transactional
    public PaymentResponse refund(UUID bookingId){
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        if(!booking.getStatus().name().equals(Booking.BookingStatus.CANCELLED.name()))
            throw new NotAuthorizedException("Can't refund an active booking");
        Payment payment = paymentRepository.findByBookingId(bookingId);
        if(payment.getStatus().equals(Payment.PaymentStatus.PENDING))
            payment.setStatus(Payment.PaymentStatus.CANCELLED);
        else if(payment.getStatus().equals(Payment.PaymentStatus.COMPLETED))
            payment.setStatus(Payment.PaymentStatus.REFUNDED);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return paymentMapper.toPaymentResponse(payment);
    }
}
