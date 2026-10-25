package com.inventory.main;

import com.inventory.exception.CustomerNotFoundException;
import com.inventory.exception.DuplicateEntityException;
import com.inventory.exception.InsufficientStockException;
import com.inventory.exception.InvalidInputException;
import com.inventory.exception.ProductNotFoundException;
import com.inventory.model.Customer;
import com.inventory.model.PremiumCustomer;
import com.inventory.model.Product;
import com.inventory.model.ProductTaxCategory;
import com.inventory.model.Purchase;
import com.inventory.model.PurchaseDetail;
import com.inventory.model.Sale;
import com.inventory.model.SaleDetail;
import com.inventory.service.BillingService;
import com.inventory.service.InventoryService;
import com.inventory.util.InputValidator;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Main console application entry point for the Inventory and Billing System.
 * Adheres strictly to Specification 1 requirements:
 * Menu Options: Add Product / Add Customer / Make a Purchase / Make a Sale / Display Products / Exit
 */
public class InventoryBillingApp {

    private final InventoryService inventoryService;
    private final BillingService billingService;
    private final InputValidator validator;
    private int purchaseCounter = 1001;
    private int saleCounter = 2001;

    public InventoryBillingApp() {
        this.inventoryService = new InventoryService();
        this.billingService = new BillingService();
        this.validator = new InputValidator(new Scanner(System.in));
    }

    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8.name()));
            System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8.name()));
        } catch (Exception ignored) {
        }
        InventoryBillingApp app = new InventoryBillingApp();
        app.run();
    }

    /**
     * Main application loop displaying the menu and handling user selection.
     */
    public void run() {
        printHeader();

        boolean running = true;
        while (running) {
            displayMenu();
            int choice = validator.readInt("Select an option (1-6): ", 1, 6);

            System.out.println();
            switch (choice) {
                case 1:
                    handleAddProduct();
                    break;
                case 2:
                    handleAddCustomer();
                    break;
                case 3:
                    handleMakePurchase();
                    break;
                case 4:
                    handleMakeSale();
                    break;
                case 5:
                    handleDisplayProducts();
                    break;
                case 6:
                    running = false;
                    System.out.println("========================================================================");
                    System.out.println("  Thank you for using the Inventory and Billing System. Goodbye!");
                    System.out.println("========================================================================");
                    break;
                default:
                    System.out.println("[Error] Invalid selection. Please choose an option between 1 and 6.");
            }
            System.out.println();
        }
    }

    private void printHeader() {
        System.out.println("========================================================================");
        System.out.println("            INVENTORY AND BILLING SYSTEM - CONSOLE APPLICATION          ");
        System.out.println("         Assignment - II: Object-Oriented Programming Mini Project      ");
        System.out.println("========================================================================");
    }

    private void displayMenu() {
        System.out.println("------------------------------------------------------------------------");
        System.out.println("                            MAIN MENU                                   ");
        System.out.println("------------------------------------------------------------------------");
        System.out.println("  1. Add Product");
        System.out.println("  2. Add Customer");
        System.out.println("  3. Make a Purchase");
        System.out.println("  4. Make a Sale");
        System.out.println("  5. Display Products");
        System.out.println("  6. Exit");
        System.out.println("------------------------------------------------------------------------");
    }

    // ========================================================================
    // 1. ADD PRODUCT
    // ========================================================================
    private void handleAddProduct() {
        System.out.println("--- [ADD NEW PRODUCT] ---");
        try {
            String productId = validator.readString("Enter Product ID (e.g. P107): ");
            String name = validator.readString("Enter Product Name: ");
            double price = validator.readPositiveDouble("Enter Selling Price (₹): ");
            int initialStock = validator.readNonNegativeInt("Enter Initial Stock Quantity: ");

            System.out.println("\nSelect Tax Category:");
            System.out.println("  1. Exempt (0% GST)");
            System.out.println("  2. Essential (5% GST)");
            System.out.println("  3. Standard (12% GST)");
            System.out.println("  4. Electronics (18% GST)");
            System.out.println("  5. Luxury (28% GST)");
            int taxOption = validator.readInt("Choose Tax Category (1-5): ", 1, 5);

            ProductTaxCategory taxCat;
            switch (taxOption) {
                case 1: taxCat = ProductTaxCategory.EXEMPT; break;
                case 2: taxCat = ProductTaxCategory.ESSENTIAL; break;
                case 3: taxCat = ProductTaxCategory.STANDARD; break;
                case 4: taxCat = ProductTaxCategory.ELECTRONICS; break;
                case 5: taxCat = ProductTaxCategory.LUXURY; break;
                default: taxCat = ProductTaxCategory.STANDARD; break;
            }

            Product product = new Product(productId, name, price, initialStock, taxCat);
            inventoryService.addProduct(product);

            System.out.println("\n[Success] Product added successfully!");
            System.out.println("Product Details: " + product);

        } catch (DuplicateEntityException | InvalidInputException e) {
            System.out.println("\n[Error] Unable to add product: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n[Unexpected Error] An unexpected error occurred: " + e.getMessage());
        }
    }

    // ========================================================================
    // 2. ADD CUSTOMER
    // ========================================================================
    private void handleAddCustomer() {
        System.out.println("--- [ADD NEW CUSTOMER] ---");
        try {
            String customerId = validator.readString("Enter Customer ID (e.g. C105): ");
            String name = validator.readString("Enter Customer Name: ");
            String contact = validator.readString("Enter Contact Number: ");
            String email = validator.readString("Enter Email Address: ");

            System.out.println("\nSelect Customer Type:");
            System.out.println("  1. Regular Customer (Standard billing, no loyalty points)");
            System.out.println("  2. Premium Customer (Earns 1 pt/₹100, discounts when pts >= 200)");
            int typeChoice = validator.readInt("Choose Customer Type (1-2): ", 1, 2);

            Customer customer;
            if (typeChoice == 2) {
                int initialPoints = validator.readNonNegativeInt("Enter Initial Reward Points: ");
                customer = new PremiumCustomer(customerId, name, contact, email, initialPoints);
            } else {
                customer = new Customer(customerId, name, contact, email);
            }

            billingService.addCustomer(customer);
            System.out.println("\n[Success] " + customer.getCustomerType() + " registered successfully!");
            System.out.println("Customer Profile: " + customer);

        } catch (DuplicateEntityException | InvalidInputException e) {
            System.out.println("\n[Error] Registration failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n[Unexpected Error] An unexpected error occurred: " + e.getMessage());
        }
    }

    // ========================================================================
    // 3. MAKE A PURCHASE (Restocking from Supplier)
    // ========================================================================
    private void handleMakePurchase() {
        System.out.println("--- [MAKE A PURCHASE / RESTOCK INVENTORY] ---");
        try {
            inventoryService.displayProductsTable();

            String purchaseId = "PUR-" + (purchaseCounter++);
            System.out.println("Generated Purchase ID: " + purchaseId);
            String supplierName = validator.readString("Enter Supplier / Distributor Name: ");

            Purchase purchase = new Purchase(purchaseId, supplierName);

            boolean addMore = true;
            while (addMore) {
                String productId = validator.readString("\nEnter Product ID to restock: ");
                Product product;
                try {
                    product = inventoryService.getProductById(productId);
                } catch (ProductNotFoundException e) {
                    System.out.println("[Error] " + e.getMessage());
                    boolean retry = validator.readBoolean("Would you like to try another product ID?");
                    if (retry) continue;
                    else break;
                }

                System.out.println("Selected Product: " + product.getName() + " (Current Stock: " + product.getStockQuantity() + ")");
                int quantity = validator.readPositiveInt("Enter Restock Quantity: ");
                double costPrice = validator.readPositiveDouble("Enter Unit Cost Price (₹): ");

                PurchaseDetail detail = new PurchaseDetail(product, quantity, costPrice);
                purchase.addPurchaseDetail(detail);

                addMore = validator.readBoolean("Add another item to this purchase order?");
            }

            if (purchase.getPurchaseDetails().isEmpty()) {
                System.out.println("\n[Notice] No items were added to the purchase order. Transaction aborted.");
                return;
            }

            // Confirm and execute purchase (Enforces Business Rule 2)
            inventoryService.recordPurchase(purchase);

            System.out.println("\n[Success] Purchase order confirmed and inventory stock updated immediately!");
            System.out.println(purchase.getPurchaseReceipt());

        } catch (InvalidInputException e) {
            System.out.println("\n[Error] Purchase transaction failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n[Unexpected Error] An unexpected error occurred: " + e.getMessage());
        }
    }

    // ========================================================================
    // 4. MAKE A SALE (Billing & Rewards)
    // ========================================================================
    private void handleMakeSale() {
        System.out.println("--- [MAKE A SALE / ISSUE INVOICE] ---");
        try {
            billingService.displayCustomersTable();

            String customerId = validator.readString("Enter Customer ID for billing: ");
            Customer customer;
            try {
                customer = billingService.getCustomerById(customerId);
            } catch (CustomerNotFoundException e) {
                System.out.println("\n[Error] " + e.getMessage());
                System.out.println("Please register the customer using Option 2 before creating a sale.");
                return;
            }

            System.out.println("\nSelected Customer: " + customer.getName() + " (" + customer.getCustomerType() + ")");
            if (customer instanceof PremiumCustomer) {
                System.out.println("Current Reward Points: " + customer.getRewardPoints() + " pts");
                if (customer.getRewardPoints() >= 200) {
                    int discountBlocks = customer.getRewardPoints() / 100;
                    System.out.println("-> Eligible for ₹" + (discountBlocks * 10) + " loyalty discount (" 
                            + (discountBlocks * 100) + " points will be redeemed).");
                } else {
                    System.out.println("-> Points below 200 threshold; no points discount applied today.");
                }
            }

            inventoryService.displayProductsTable();

            String saleId = "INV-" + (saleCounter++);
            Sale sale = new Sale(saleId, customer);

            boolean addMore = true;
            while (addMore) {
                String productId = validator.readString("\nEnter Product ID to sell: ");
                Product product;
                try {
                    product = inventoryService.getProductById(productId);
                } catch (ProductNotFoundException e) {
                    System.out.println("[Error] " + e.getMessage());
                    boolean retry = validator.readBoolean("Try another product ID?");
                    if (retry) continue;
                    else break;
                }

                System.out.printf("Product: %s | Price: ₹%.2f | Available Stock: %d\n",
                        product.getName(), product.getPrice(), product.getStockQuantity());

                int quantity = validator.readPositiveInt("Enter Sale Quantity: ");

                // Early check before adding detail
                if (product.getStockQuantity() < quantity) {
                    System.out.printf("  [Warning] Insufficient stock! Available: %d, Requested: %d.\n",
                            product.getStockQuantity(), quantity);
                    boolean tryDifferentQty = validator.readBoolean("Do you want to re-enter quantity for this item?");
                    if (tryDifferentQty) {
                        continue;
                    } else {
                        boolean cont = validator.readBoolean("Add a different product instead?");
                        if (cont) continue;
                        else break;
                    }
                }

                SaleDetail detail = new SaleDetail(product, quantity);
                sale.addSaleDetail(detail);

                addMore = validator.readBoolean("Add another product to this invoice?");
            }

            if (sale.getSaleDetails().isEmpty()) {
                System.out.println("\n[Notice] No products added. Sale invoice cancelled.");
                return;
            }

            // Confirm and execute the sale
            // Enforces Business Rules 1, 2, 3, 4, 5
            billingService.recordSale(sale);

            System.out.println("\n[Success] Sale confirmed and completed successfully!");
            System.out.println(sale.getSaleBill());

        } catch (InsufficientStockException e) {
            System.out.println("\n[Business Rule 1 Violation - Sale Aborted]");
            System.out.println(e.getMessage());
            System.out.println("Action: No stock was deducted. The sale has been terminated.");
        } catch (InvalidInputException e) {
            System.out.println("\n[Error] Sale processing failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n[Unexpected Error] An unexpected error occurred: " + e.getMessage());
        }
    }

    // ========================================================================
    // 5. DISPLAY PRODUCTS
    // ========================================================================
    private void handleDisplayProducts() {
        inventoryService.displayProductsTable();
    }
}
