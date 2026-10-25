package com.inventory.model;

import com.inventory.exception.InsufficientStockException;
import com.inventory.exception.InvalidInputException;
import java.io.Serializable;

/**
 * Represents an inventory product.
 * Implements encapsulation and links directly to a ProductTaxCategory.
 */
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    private String productId;
    private String name;
    private double price;
    private int stockQuantity;
    private ProductTaxCategory taxCategory;

    public Product() {
    }

    public Product(String productId, String name, double price, int stockQuantity, ProductTaxCategory taxCategory) 
            throws InvalidInputException {
        if (productId == null || productId.trim().isEmpty()) {
            throw new InvalidInputException("Product ID cannot be null or empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Product Name cannot be null or empty.");
        }
        if (price < 0) {
            throw new InvalidInputException("Product price cannot be negative.");
        }
        if (stockQuantity < 0) {
            throw new InvalidInputException("Stock quantity cannot be negative.");
        }
        this.productId = productId.trim().toUpperCase();
        this.name = name.trim();
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.taxCategory = (taxCategory != null) ? taxCategory : ProductTaxCategory.STANDARD;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) throws InvalidInputException {
        if (productId == null || productId.trim().isEmpty()) {
            throw new InvalidInputException("Product ID cannot be empty.");
        }
        this.productId = productId.trim().toUpperCase();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws InvalidInputException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Product name cannot be empty.");
        }
        this.name = name.trim();
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) throws InvalidInputException {
        if (price < 0) {
            throw new InvalidInputException("Price cannot be negative.");
        }
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) throws InvalidInputException {
        if (stockQuantity < 0) {
            throw new InvalidInputException("Stock quantity cannot be negative.");
        }
        this.stockQuantity = stockQuantity;
    }

    public ProductTaxCategory getTaxCategory() {
        return taxCategory;
    }

    public void setTaxCategory(ProductTaxCategory taxCategory) {
        this.taxCategory = (taxCategory != null) ? taxCategory : ProductTaxCategory.STANDARD;
    }

    /**
     * Increases stock quantity immediately after a purchase.
     * Enforces Business Rule 2.
     */
    public synchronized void addStock(int quantity) throws InvalidInputException {
        if (quantity <= 0) {
            throw new InvalidInputException("Quantity to add must be positive.");
        }
        this.stockQuantity += quantity;
    }

    /**
     * Verifies and decreases stock quantity immediately after a sale.
     * Enforces Business Rules 1 & 2.
     */
    public synchronized void deductStock(int quantity) throws InsufficientStockException, InvalidInputException {
        if (quantity <= 0) {
            throw new InvalidInputException("Quantity to deduct must be positive.");
        }
        if (this.stockQuantity < quantity) {
            throw new InsufficientStockException(this.productId, quantity, this.stockQuantity);
        }
        this.stockQuantity -= quantity;
    }

    /**
     * Calculates tax amount per unit of this product based on its tax category.
     */
    public double getTaxAmountPerUnit() {
        if (taxCategory == null) return 0.0;
        return (price * taxCategory.getGstRate()) / 100.0;
    }

    @Override
    public String toString() {
        return String.format("%-8s | %-22s | ₹%-9.2f | Stock: %-5d | Tax: %s",
                productId, name, price, stockQuantity, (taxCategory != null ? taxCategory.toString() : "None"));
    }
}
