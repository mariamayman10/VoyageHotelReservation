package org.example.voyage.booking;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.voyage.booking.dto.BookingRequest;

public class CheckOutAfterCheckInValidator implements ConstraintValidator<CheckOutAfterCheckIn, BookingRequest> {
    @Override
    public boolean isValid(BookingRequest bookingRequest, ConstraintValidatorContext context) {
        if (bookingRequest.getCheckInDate() == null || bookingRequest.getCheckOutDate() == null) {
            return true;
        }
        boolean valid = bookingRequest.getCheckOutDate().isAfter(bookingRequest.getCheckInDate());
        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Check-out date must be after check-in date")
                    .addPropertyNode("checkOutDate")
                    .addConstraintViolation();
        }
        return valid;
    }
}
