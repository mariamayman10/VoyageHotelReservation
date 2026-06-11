package org.example.voyage.exception;

public class MissingPathVariableException extends RuntimeException {
    public MissingPathVariableException(String msg){
        super(msg);
    }
}
