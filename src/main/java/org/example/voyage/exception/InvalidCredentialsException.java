package org.example.voyage.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String msg){
        super(msg);
    }
}
