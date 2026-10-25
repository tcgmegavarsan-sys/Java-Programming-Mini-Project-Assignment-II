package com.inventory.model;

import com.inventory.exception.InsufficientStockException;
import com.inventory.exception.InvalidInputException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a completed sales transaction and tax invoice.
 * Demonstrates:
 * - Aggregation: Customer reference (polymorphic dispatch for discounts and reward points)
 * - Composition: List of SaleDetail objects
 * - Business Rules:
 *   1. Atomic stock verification prior to confirmation.
 *   2. Immediate stock decrement upon confirmation.
 *   3. Reward points redemption and accrual for PremiumCustomer.
 *   4. Zero points and zero discount for regular Customer.
 *   5. Sale bill formatting displaying all required breakdown details.
 */
public class Sale implements Serializable {
    private static final long serialVersionUID = 1L;

    private String saleId;
    private LocalDateTime saleDate;
    private Customer customer;
    private List<SaleDetail> saleDetails;

    // Financial totals
    private double grossTotal;
    private double discountAmount;
    private int pointsRedeemed;
    private double subtotalAfterDiscount;
    private double totalTaxAmount;
    private double netTotal;

    // Loyalty points tracking
    private int pointsBeforeSale;
    private int pointsEarned;
    private int pointsAfterSale;

    private boolean processed;

    public Sale() {
        this.saleDetails = new ArrayList<>();
        this.saleDate = LocalDateTime.now();
        this.processed = false;
    }

    public Sale(String saleId, Customer customer) throws InvalidInputException {
        if (saleId == null || saleId.trim().isEmpty()) {
            throw new InvalidInputException("Sale ID cannot be empty.");
        }
        if (customer == null) {
            throw new InvalidInputException("Customer cannot be null for a sale.");
        }
        this.saleId = saleId.trim().toUpperCase();
        this.customer = customer;
        this.saleDate = LocalDateTime.now();
        this.saleDetails = new ArrayList<>();
        this.processed = false;
    }

    public String getSaleId() {
        return saleId;
    }

    public void setSaleId(String saleId) throws InvalidInputException {
        if (saleId == null || saleId.trim().isEmpty()) {
            throw new InvalidInputException("Sale ID cannot be empty.");
        }
        this.saleId = saleId.trim().toUpperCase();
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) throws InvalidInputException {
        if (customer == null) {
            throw new InvalidInputException("Customer cannot be null.");
        }
        this.customer = customer;
    }

    public List<SaleDetail> getSaleDetails() {
        return Collections.unmodifiableList(saleDetails);
    }

    public double getGrossTotal() {
        return grossTotal;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public int getPointsRedeemed() {
        return pointsRedeemed;
    }

    public double getSubtotalAfterDiscount() {
        return subtotalAfterDiscount;
    }

    public double getTotalTaxAmount() {
        return totalTaxAmount;
    }

    public double getNetTotal() {
        return netTotal;
    }

    public int getPointsBeforeSale() {
        return pointsBeforeSale;
    }

    public int getPointsEarned() {
        return pointsEarned;
    }

    public int getPointsAfterSale() {
        return pointsAfterSale;
    }

    public boolean isProcessed() {
        return processed;
    }

    /**
     * Adds an item to the sale before processing.
     */
    public void addSaleDetail(SaleDetail detail) throws InvalidInputException {
        if (detail == null) {
            throw new InvalidInputException("Sale detail cannot be null.");
        }
        if (processed) {
            throw new InvalidInputException("Cannot add items to a finalized sale.");
        }
        saleDetails.add(detail);
    }

    /**
     * Processes and finalizes the sale adhering to all business rules:
     * 1. Verifies stock availability for ALL items before deducting any stock (Rule 1).
     * 2. Calculates gross total across all sale details.
     * 3. Calculates customer discount and points redemption polymorphically (Rules 3 & 4).
     * 4. Deducts stock immediately upon confirmation (Rule 2).
     * 5. Accrues new reward points for premium customer (Rule 3).
     */
    public synchronized void processSale() throws InsufficientStockException, InvalidInputException {
        if (processed) {
            throw new InvalidInputException("This sale has already been processed and finalized.");
        }
        if (saleDetails.isEmpty()) {
            throw new InvalidInputException("Cannot process a sale with no items.");
        }

        // --- BUSINESS RULE 1: Stock verification before confirming sale ---
        for (SaleDetail detail : saleDetails) {
            Product prod = detail.getProduct();
            if (prod.getStockQuantity() < detail.getQuantity()) {
                throw new InsufficientStockException(prod.getProductId(), detail.getQuantity(), prod.getStockQuantity());
            }
        }

        // Calculate gross total (before discount and tax)
        this.grossTotal = 0.0;
        for (SaleDetail detail : saleDetails) {
            this.grossTotal += detail.getGrossAmount();
        }

        // Record initial customer reward points
        this.pointsBeforeSale = customer.getRewardPoints();

        // --- BUSINESS RULE 3 & 4: Polymorphic discount and points handling ---
        this.discountAmount = customer.calculateDiscount(this.grossTotal);
        this.pointsRedeemed = customer.redeemPointsForDiscount(this.discountAmount);

        // Subtotal after discount, before tax
        this.subtotalAfterDiscount = Math.max(0.0, this.grossTotal - this.discountAmount);

        // Calculate tax based on proportion after discount
        double discountRatio = (this.grossTotal > 0.0) ? (this.subtotalAfterDiscount / this.grossTotal) : 1.0;
        this.totalTaxAmount = 0.0;
        for (SaleDetail detail : saleDetails) {
            double lineGross = detail.getGrossAmount();
            double lineTaxable = lineGross * discountRatio;
            double lineTax = (lineTaxable * detail.getTaxRate()) / 100.0;
            this.totalTaxAmount += lineTax;
        }

        // Net total payable
        this.netTotal = this.subtotalAfterDiscount + this.totalTaxAmount;

        // --- BUSINESS RULE 2: Immediate stock deduction ---
        for (SaleDetail detail : saleDetails) {
            detail.getProduct().deductStock(detail.getQuantity());
        }

        // --- BUSINESS RULE 3: Reward points earning for Premium Customers ---
        // Earns 1 point per ₹100 spent (after discounts, before tax)
        this.pointsEarned = customer.calculatePointsEarned(this.subtotalAfterDiscount);
        customer.addRewardPoints(this.pointsEarned);
        this.pointsAfterSale = customer.getRewardPoints();

        this.processed = true;
    }

    /**
     * Generates a fully formatted sale bill meeting all Expected Output criteria:
     * - Product list, quantities, prices, taxes, discounts, total amount,
     * - Updated reward points (for premium customers),
     * - Updated stock after sale.
     */
    public String getSaleBill() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");
        StringBuilder sb = new StringBuilder();

        sb.append("========================================================================================\n");
        sb.append("                               TAX INVOICE / SALE BILL                                  \n");
        sb.append("========================================================================================\n");
        sb.append(String.format("Invoice No : %-30s Date     : %s\n", saleId, saleDate.format(dtf)));
        sb.append(String.format("Customer   : %-30s Type     : %s\n", customer.getName(), customer.getCustomerType()));
        sb.append(String.format("Cust ID    : %-30s Contact  : %s\n", customer.getCustomerId(), customer.getContactNumber()));
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(String.format("%-8s %-22s %6s %10s %8s %11s %14s\n", 
                "Item ID", "Product Description", "Qty", "Price(₹)", "GST %", "Tax(₹)", "Line Total(₹)"));
        sb.append("----------------------------------------------------------------------------------------\n");

        double discountRatio = (this.grossTotal > 0.0) ? (this.subtotalAfterDiscount / this.grossTotal) : 1.0;
        for (SaleDetail d : saleDetails) {
            double lineTaxable = d.getGrossAmount() * discountRatio;
            double itemTax = (lineTaxable * d.getTaxRate()) / 100.0;
            double lineTotalWithTax = lineTaxable + itemTax;

            sb.append(String.format("%-8s %-22s %6d %10.2f %7.1f%% %11.2f %14.2f\n",
                    d.getProduct().getProductId(),
                    d.getProduct().getName(),
                    d.getQuantity(),
                    d.getUnitPrice(),
                    d.getTaxRate(),
                    itemTax,
                    lineTotalWithTax));
        }

        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(String.format("Gross Total (Before Discount & Tax) :                             ₹%12.2f\n", grossTotal));
        
        if (discountAmount > 0.0) {
            sb.append(String.format("Loyalty Discount Applied (%d pts)  :                            -₹%12.2f\n", pointsRedeemed, discountAmount));
            sb.append(String.format("Subtotal (After Discount, Pre-Tax)  :                             ₹%12.2f\n", subtotalAfterDiscount));
        }
        
        sb.append(String.format("Applicable Goods & Services Tax (GST):                            ₹%12.2f\n", totalTaxAmount));
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(String.format("NET TOTAL AMOUNT PAYABLE            :                             ₹%12.2f\n", netTotal));
        sb.append("========================================================================================\n");

        // Reward points summary for premium customers (Rule 3 & 4)
        if (customer instanceof PremiumCustomer) {
            sb.append("REWARD POINTS SUMMARY (PREMIUM CUSTOMER):\n");
            sb.append(String.format("  • Opening Points Balance     : %d pts\n", pointsBeforeSale));
            if (pointsRedeemed > 0) {
                sb.append(String.format("  • Points Redeemed in Invoice : -%d pts (₹%.2f discount)\n", pointsRedeemed, discountAmount));
            }
            sb.append(String.format("  • Points Earned from Purchase: +%d pts (1 pt per ₹100 spent)\n", pointsEarned));
            sb.append(String.format("  • Closing Reward Balance     : %d pts\n", pointsAfterSale));
            sb.append("----------------------------------------------------------------------------------------\n");
        } else {
            sb.append("CUSTOMER REWARD STATUS:\n");
            sb.append("  • Regular Customer (No reward points or loyalty discounts applicable)\n");
            sb.append("----------------------------------------------------------------------------------------\n");
        }

        // Updated Stock after sale
        sb.append("UPDATED INVENTORY STOCK STATUS (AFTER SALE):\n");
        for (SaleDetail d : saleDetails) {
            sb.append(String.format("  • %-22s (%s) : Remaining Stock = %d units\n",
                    d.getProduct().getName(),
                    d.getProduct().getProductId(),
                    d.getProduct().getStockQuantity()));
        }
        sb.append("========================================================================================\n");

        return sb.toString();
    }
}
