package com.inventory.model;

import com.inventory.exception.InvalidInputException;
import java.io.Serializable;

/**
 * Base class representing a customer in the inventory and billing system.
 * Demonstrates Encapsulation and acts as the parent for polymorphic customer types.
 */
public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    private String customerId;
    private String name;
    private String contactNumber;
    private String email;

    public Customer() {
    }

    public Customer(String customerId, String name, String contactNumber, String email) 
            throws InvalidInputException {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new InvalidInputException("Customer ID cannot be empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Customer Name cannot be empty.");
        }
        this.customerId = customerId.trim().toUpperCase();
        this.name = name.trim();
        this.contactNumber = (contactNumber != null) ? contactNumber.trim() : "";
        this.email = (email != null) ? email.trim() : "";
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) throws InvalidInputException {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new InvalidInputException("Customer ID cannot be empty.");
        }
        this.customerId = customerId.trim().toUpperCase();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws InvalidInputException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Customer Name cannot be empty.");
        }
        this.name = name.trim();
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = (contactNumber != null) ? contactNumber.trim() : "";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = (email != null) ? email.trim() : "";
    }

    /**
     * Polymorphic method returning the customer category.
     */
    public String getCustomerType() {
        return "Regular";
    }

    /**
     * Polymorphic method calculating eligible discount before tax.
     * Business Rule 4: Regular customers do not receive points or discounts.
     */
    public double calculateDiscount(double spentBeforeTax) {
        return 0.0;
    }

    /**
     * Polymorphic method calculating earned reward points.
     * Business Rule 4: Regular customers do not receive points or discounts.
     */
    public int calculatePointsEarned(double amountAfterDiscountBeforeTax) {
        return 0;
    }

    /**
     * Deducts redeemed reward points from the customer balance.
     * No-op for regular customers.
     */
    public int redeemPointsForDiscount(double discountApplied) {
        return 0;
    }

    /**
     * Returns current reward points. Regular customers have 0.
     */
    public int getRewardPoints() {
        return 0;
    }

    /**
     * Adds reward points to customer account. No-op for regular customers.
     */
    public void addRewardPoints(int points) {
        // Regular customers do not collect points
    }

    @Override
    public String toString() {
        return String.format("%-8s | %-20s | %-12s | %-10s | %s",
                customerId, name, contactNumber, getCustomerType(), email);
    }
}
