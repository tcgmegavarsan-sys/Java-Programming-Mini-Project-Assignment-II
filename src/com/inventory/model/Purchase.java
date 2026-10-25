package com.inventory.model;

import com.inventory.exception.InvalidInputException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a purchase/restocking transaction from a supplier.
 * Demonstrates Composition by owning a list of PurchaseDetail items.
 * Enforces Business Rule 2: Stock quantity must be updated immediately after a purchase.
 */
public class Purchase implements Serializable {
    private static final long serialVersionUID = 1L;

    private String purchaseId;
    private LocalDateTime purchaseDate;
    private String supplierName;
    private List<PurchaseDetail> purchaseDetails;
    private double totalAmount;
    private boolean processed;

    public Purchase() {
        this.purchaseDetails = new ArrayList<>();
        this.purchaseDate = LocalDateTime.now();
        this.processed = false;
    }

    public Purchase(String purchaseId, String supplierName) throws InvalidInputException {
        if (purchaseId == null || purchaseId.trim().isEmpty()) {
            throw new InvalidInputException("Purchase ID cannot be empty.");
        }
        if (supplierName == null || supplierName.trim().isEmpty()) {
            throw new InvalidInputException("Supplier name cannot be empty.");
        }
        this.purchaseId = purchaseId.trim().toUpperCase();
        this.supplierName = supplierName.trim();
        this.purchaseDate = LocalDateTime.now();
        this.purchaseDetails = new ArrayList<>();
        this.totalAmount = 0.0;
        this.processed = false;
    }

    public String getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(String purchaseId) throws InvalidInputException {
        if (purchaseId == null || purchaseId.trim().isEmpty()) {
            throw new InvalidInputException("Purchase ID cannot be empty.");
        }
        this.purchaseId = purchaseId.trim().toUpperCase();
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) throws InvalidInputException {
        if (supplierName == null || supplierName.trim().isEmpty()) {
            throw new InvalidInputException("Supplier name cannot be empty.");
        }
        this.supplierName = supplierName.trim();
    }

    public List<PurchaseDetail> getPurchaseDetails() {
        return Collections.unmodifiableList(purchaseDetails);
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public boolean isProcessed() {
        return processed;
    }

    /**
     * Adds an item detail to the purchase.
     */
    public void addPurchaseDetail(PurchaseDetail detail) throws InvalidInputException {
        if (detail == null) {
            throw new InvalidInputException("Purchase detail cannot be null.");
        }
        if (processed) {
            throw new InvalidInputException("Cannot add details to an already processed purchase.");
        }
        purchaseDetails.add(detail);
        totalAmount += detail.getSubtotal();
    }

    /**
     * Confirms the purchase and immediately updates product stock.
     * Enforces Business Rule 2.
     */
    public synchronized void processPurchase() throws InvalidInputException {
        if (processed) {
            throw new InvalidInputException("Purchase has already been processed.");
        }
        if (purchaseDetails.isEmpty()) {
            throw new InvalidInputException("Cannot process an empty purchase order.");
        }
        for (PurchaseDetail detail : purchaseDetails) {
            detail.getProduct().addStock(detail.getQuantity());
        }
        this.processed = true;
    }

    /**
     * Generates a detailed receipt for the purchase order.
     */
    public String getPurchaseReceipt() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================\n");
        sb.append("                     PURCHASE / RESTOCK RECEIPT                         \n");
        sb.append("========================================================================\n");
        sb.append(String.format("Purchase ID : %-25s Date: %s\n", purchaseId, purchaseDate.format(dtf)));
        sb.append(String.format("Supplier    : %s\n", supplierName));
        sb.append("------------------------------------------------------------------------\n");
        sb.append(String.format("%-10s %-24s %8s %12s %14s\n", "Prod ID", "Product Name", "Qty", "Cost (₹)", "Subtotal (₹)"));
        sb.append("------------------------------------------------------------------------\n");
        for (PurchaseDetail d : purchaseDetails) {
            sb.append(String.format("%-10s %-24s %8d %12.2f %14.2f\n",
                    d.getProduct().getProductId(),
                    d.getProduct().getName(),
                    d.getQuantity(),
                    d.getUnitCostPrice(),
                    d.getSubtotal()));
        }
        sb.append("------------------------------------------------------------------------\n");
        sb.append(String.format("TOTAL PURCHASE AMOUNT:                                ₹%14.2f\n", totalAmount));
        sb.append("------------------------------------------------------------------------\n");
        sb.append("UPDATED PRODUCT STOCK LEVELS:\n");
        for (PurchaseDetail d : purchaseDetails) {
            sb.append(String.format("  • %s (%s): Current Available Stock = %d units\n",
                    d.getProduct().getName(),
                    d.getProduct().getProductId(),
                    d.getProduct().getStockQuantity()));
        }
        sb.append("========================================================================\n");
        return sb.toString();
    }
}
