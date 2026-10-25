package com.inventory.model;

import java.io.Serializable;

/**
 * Represents the tax category applicable to products in the inventory system.
 * Encapsulates GST/tax rates and category metadata.
 */
public class ProductTaxCategory implements Serializable {
    private static final long serialVersionUID = 1L;

    private String categoryId;
    private String categoryName;
    private double gstRate; // in percentage, e.g., 5.0, 12.0, 18.0

    // Standard pre-defined tax categories
    public static final ProductTaxCategory EXEMPT = new ProductTaxCategory("TC01", "Exempt (0%)", 0.0);
    public static final ProductTaxCategory ESSENTIAL = new ProductTaxCategory("TC02", "Essential (5%)", 5.0);
    public static final ProductTaxCategory STANDARD = new ProductTaxCategory("TC03", "Standard (12%)", 12.0);
    public static final ProductTaxCategory ELECTRONICS = new ProductTaxCategory("TC04", "Electronics (18%)", 18.0);
    public static final ProductTaxCategory LUXURY = new ProductTaxCategory("TC05", "Luxury (28%)", 28.0);

    public ProductTaxCategory() {
    }

    public ProductTaxCategory(String categoryId, String categoryName, double gstRate) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.gstRate = Math.max(0.0, gstRate);
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public double getGstRate() {
        return gstRate;
    }

    public void setGstRate(double gstRate) {
        this.gstRate = Math.max(0.0, gstRate);
    }

    @Override
    public String toString() {
        return categoryName + " [" + gstRate + "%]";
    }
}
