package org.example.voyage.exception;

public class OperationCanNotBeCompleted extends RuntimeException {
    public OperationCanNotBeCompleted(String message) {
        super(message);
    }
}
