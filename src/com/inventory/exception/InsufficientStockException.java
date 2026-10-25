package com.inventory.exception;

/**
 * Thrown when a sale cannot proceed due to insufficient stock in the inventory.
 * Enforces Business Rule 1: Stock must be verified before confirming a sale.
 */
public class InsufficientStockException extends Exception {
    private static final long serialVersionUID = 1L;

    private final String productId;
    private final int requestedQuantity;
    private final int availableQuantity;

    public InsufficientStockException(String message) {
        super(message);
        this.productId = "";
        this.requestedQuantity = 0;
        this.availableQuantity = 0;
    }

    public InsufficientStockException(String productId, int requestedQuantity, int availableQuantity) {
        super("Insufficient stock for Product ID [" + productId + "]. Requested: " 
                + requestedQuantity + ", Available: " + availableQuantity);
        this.productId = productId;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    public String getProductId() {
        return productId;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }
}
