package com.inventory.exception;

/**
 * Thrown when an invalid value is supplied by the user during data entry or processing.
 */
public class InvalidInputException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidInputException(String message) {
        super(message);
    }
}
