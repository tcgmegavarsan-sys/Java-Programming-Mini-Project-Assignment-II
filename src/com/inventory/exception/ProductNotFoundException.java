package com.inventory.exception;

/**
 * Thrown when an operation references a product identifier that does not exist in the inventory.
 */
public class ProductNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public ProductNotFoundException(String message) {
        super(message);
    }

    public ProductNotFoundException(String productId, boolean isId) {
        super("Product with ID '" + productId + "' was not found in the inventory system.");
    }
}
