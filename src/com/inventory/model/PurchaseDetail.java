package com.inventory.model;

import com.inventory.exception.InvalidInputException;
import java.io.Serializable;

/**
 * Represents an individual line item in a restock/purchase order.
 * Demonstrates Aggregation by directly linking to an associated Product (Business Rule 5).
 */
public class PurchaseDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    private Product product;
    private int quantity;
    private double unitCostPrice;

    public PurchaseDetail() {
    }

    public PurchaseDetail(Product product, int quantity, double unitCostPrice) throws InvalidInputException {
        if (product == null) {
            throw new InvalidInputException("Associated product cannot be null.");
        }
        if (quantity <= 0) {
            throw new InvalidInputException("Purchase quantity must be greater than zero.");
        }
        if (unitCostPrice < 0) {
            throw new InvalidInputException("Unit cost price cannot be negative.");
        }
        this.product = product;
        this.quantity = quantity;
        this.unitCostPrice = unitCostPrice;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) throws InvalidInputException {
        if (product == null) {
            throw new InvalidInputException("Associated product cannot be null.");
        }
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) throws InvalidInputException {
        if (quantity <= 0) {
            throw new InvalidInputException("Purchase quantity must be greater than zero.");
        }
        this.quantity = quantity;
    }

    public double getUnitCostPrice() {
        return unitCostPrice;
    }

    public void setUnitCostPrice(double unitCostPrice) throws InvalidInputException {
        if (unitCostPrice < 0) {
            throw new InvalidInputException("Unit cost price cannot be negative.");
        }
        this.unitCostPrice = unitCostPrice;
    }

    public double getSubtotal() {
        return quantity * unitCostPrice;
    }

    @Override
    public String toString() {
        return String.format("%-8s | %-20s | %5d units @ ₹%-8.2f = ₹%-10.2f",
                product.getProductId(), product.getName(), quantity, unitCostPrice, getSubtotal());
    }
}
