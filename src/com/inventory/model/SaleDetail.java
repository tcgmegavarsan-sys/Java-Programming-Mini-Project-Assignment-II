package com.inventory.model;

import com.inventory.exception.InvalidInputException;
import java.io.Serializable;

/**
 * Represents an individual line item in a sales invoice.
 * Demonstrates Aggregation by directly linking to an associated Product (Business Rule 5).
 */
public class SaleDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    private Product product;
    private int quantity;
    private double unitPrice;
    private double taxRate; // in %

    public SaleDetail() {
    }

    public SaleDetail(Product product, int quantity) throws InvalidInputException {
        if (product == null) {
            throw new InvalidInputException("Associated product cannot be null.");
        }
        if (quantity <= 0) {
            throw new InvalidInputException("Sale quantity must be greater than zero.");
        }
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
        this.taxRate = (product.getTaxCategory() != null) ? product.getTaxCategory().getGstRate() : 0.0;
    }

    public SaleDetail(Product product, int quantity, double unitPrice) throws InvalidInputException {
        if (product == null) {
            throw new InvalidInputException("Associated product cannot be null.");
        }
        if (quantity <= 0) {
            throw new InvalidInputException("Sale quantity must be greater than zero.");
        }
        if (unitPrice < 0) {
            throw new InvalidInputException("Unit price cannot be negative.");
        }
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.taxRate = (product.getTaxCategory() != null) ? product.getTaxCategory().getGstRate() : 0.0;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) throws InvalidInputException {
        if (product == null) {
            throw new InvalidInputException("Associated product cannot be null.");
        }
        this.product = product;
        this.taxRate = (product.getTaxCategory() != null) ? product.getTaxCategory().getGstRate() : 0.0;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) throws InvalidInputException {
        if (quantity <= 0) {
            throw new InvalidInputException("Quantity must be greater than zero.");
        }
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) throws InvalidInputException {
        if (unitPrice < 0) {
            throw new InvalidInputException("Unit price cannot be negative.");
        }
        this.unitPrice = unitPrice;
    }

    public double getTaxRate() {
        return taxRate;
    }

    /**
     * Calculates gross amount before tax and discount for this line item.
     */
    public double getGrossAmount() {
        return quantity * unitPrice;
    }

    /**
     * Calculates tax amount for this line item based on its tax rate.
     */
    public double getTaxAmount() {
        return (getGrossAmount() * taxRate) / 100.0;
    }

    /**
     * Returns gross amount plus tax for this line item.
     */
    public double getLineTotal() {
        return getGrossAmount() + getTaxAmount();
    }

    @Override
    public String toString() {
        return String.format("%-8s | %-20s | %5d | ₹%-8.2f | %4.1f%% | ₹%-8.2f | ₹%-9.2f",
                product.getProductId(), product.getName(), quantity, unitPrice, taxRate, getTaxAmount(), getLineTotal());
    }
}
