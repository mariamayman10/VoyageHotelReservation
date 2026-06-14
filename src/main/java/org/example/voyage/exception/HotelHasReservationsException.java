package org.example.voyage.exception;

public class HotelHasReservationsException extends RuntimeException {
    public HotelHasReservationsException(String message) {
        super(message);
    }
}
