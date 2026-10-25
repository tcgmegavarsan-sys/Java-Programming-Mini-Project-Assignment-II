package com.inventory.exception;

/**
 * Thrown when attempting to register an entity with an identifier that already exists.
 */
public class DuplicateEntityException extends Exception {
    private static final long serialVersionUID = 1L;

    public DuplicateEntityException(String message) {
        super(message);
    }
}
