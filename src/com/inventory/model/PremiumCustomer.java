package com.inventory.model;

import com.inventory.exception.InvalidInputException;

/**
 * Represents a premium customer in the inventory system.
 * Extends Customer to demonstrate Inheritance and Polymorphism.
 * Enforces Business Rule 3:
 * - Earns 1 point for every ₹100 spent (after discounts, before tax).
 * - When points reach 200 or more, ₹10 discount per 100 points is applied, and points are deducted.
 */
public class PremiumCustomer extends Customer {
    private static final long serialVersionUID = 1L;

    private int rewardPoints;
    private String membershipTier;

    public PremiumCustomer() {
        super();
        this.rewardPoints = 0;
        this.membershipTier = "Gold";
    }

    public PremiumCustomer(String customerId, String name, String contactNumber, String email, int initialPoints) 
            throws InvalidInputException {
        super(customerId, name, contactNumber, email);
        if (initialPoints < 0) {
            throw new InvalidInputException("Initial reward points cannot be negative.");
        }
        this.rewardPoints = initialPoints;
        this.membershipTier = determineTier(initialPoints);
    }

    private String determineTier(int points) {
        if (points >= 1000) return "Platinum";
        if (points >= 500) return "Gold";
        return "Silver";
    }

    @Override
    public String getCustomerType() {
        return "Premium (" + membershipTier + ")";
    }

    @Override
    public int getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(int rewardPoints) throws InvalidInputException {
        if (rewardPoints < 0) {
            throw new InvalidInputException("Reward points cannot be negative.");
        }
        this.rewardPoints = rewardPoints;
        this.membershipTier = determineTier(rewardPoints);
    }

    public String getMembershipTier() {
        return membershipTier;
    }

    public void setMembershipTier(String membershipTier) {
        this.membershipTier = membershipTier;
    }

    /**
     * Polymorphic implementation of discount calculation.
     * Rule 3: Once points reach 200 or more, a discount of ₹10 per 100 points is applied.
     */
    @Override
    public double calculateDiscount(double spentBeforeTax) {
        if (this.rewardPoints < 200 || spentBeforeTax <= 0) {
            return 0.0;
        }
        int eligibleBlocks = this.rewardPoints / 100;
        double potentialDiscount = eligibleBlocks * 10.0;
        
        // Discount cannot exceed the gross amount before tax
        if (potentialDiscount > spentBeforeTax) {
            int applicableBlocks = (int) (spentBeforeTax / 10.0);
            return applicableBlocks * 10.0;
        }
        return potentialDiscount;
    }

    /**
     * Polymorphic calculation of reward points earned.
     * Rule 3: Earns 1 point for every ₹100 spent (after discounts, before tax).
     */
    @Override
    public int calculatePointsEarned(double amountAfterDiscountBeforeTax) {
        if (amountAfterDiscountBeforeTax < 100.0) {
            return 0;
        }
        return (int) (amountAfterDiscountBeforeTax / 100.0);
    }

    /**
     * Deducts points corresponding to the applied discount.
     * Rule 3: Redeemed points are deducted (100 points per ₹10 discount).
     */
    @Override
    public int redeemPointsForDiscount(double discountApplied) {
        if (discountApplied <= 0) {
            return 0;
        }
        int pointsToDeduct = ((int) Math.round(discountApplied / 10.0)) * 100;
        if (pointsToDeduct > this.rewardPoints) {
            pointsToDeduct = this.rewardPoints;
        }
        this.rewardPoints -= pointsToDeduct;
        this.membershipTier = determineTier(this.rewardPoints);
        return pointsToDeduct;
    }

    /**
     * Updates customer reward points with newly earned points.
     */
    @Override
    public void addRewardPoints(int points) {
        if (points > 0) {
            this.rewardPoints += points;
            this.membershipTier = determineTier(this.rewardPoints);
        }
    }

    @Override
    public String toString() {
        return String.format("%-8s | %-20s | %-12s | %-10s | %-18s | Points: %d",
                getCustomerId(), getName(), getContactNumber(), "Premium", getEmail(), rewardPoints);
    }
}
