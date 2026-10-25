package com.inventory.exception;

/**
 * Thrown when an operation references a customer ID that does not exist.
 */
public class CustomerNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public CustomerNotFoundException(String message) {
        super(message);
    }

    public CustomerNotFoundException(String customerId, boolean isId) {
        super("Customer with ID '" + customerId + "' was not found in the system.");
    }
}
