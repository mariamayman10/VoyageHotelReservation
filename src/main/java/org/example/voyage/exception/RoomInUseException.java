package org.example.voyage.exception;

public class RoomInUseException extends RuntimeException {
    public RoomInUseException(String message) {
        super(message);
    }
}
