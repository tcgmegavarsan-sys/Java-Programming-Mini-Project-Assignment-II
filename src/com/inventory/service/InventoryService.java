package com.inventory.service;

import com.inventory.exception.DuplicateEntityException;
import com.inventory.exception.InvalidInputException;
import com.inventory.exception.ProductNotFoundException;
import com.inventory.model.Product;
import com.inventory.model.ProductTaxCategory;
import com.inventory.model.Purchase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service handling inventory management, product catalog, and supplier purchases.
 */
public class InventoryService {
    private final Map<String, Product> productCatalog = new LinkedHashMap<>();
    private final List<Purchase> purchaseHistory = new ArrayList<>();

    public InventoryService() {
        seedInitialProducts();
    }

    /**
     * Seeds initial standard products for immediate testing.
     */
    private void seedInitialProducts() {
        try {
            addProduct(new Product("P101", "Wireless Gaming Mouse", 1499.00, 30, ProductTaxCategory.ELECTRONICS));
            addProduct(new Product("P102", "Mechanical Keyboard", 3499.00, 20, ProductTaxCategory.ELECTRONICS));
            addProduct(new Product("P103", "USB-C Fast Charger 65W", 1299.00, 25, ProductTaxCategory.ELECTRONICS));
            addProduct(new Product("P104", "Organic Green Tea 250g", 350.00, 50, ProductTaxCategory.ESSENTIAL));
            addProduct(new Product("P105", "Executive Diary & Pen", 450.00, 40, ProductTaxCategory.STANDARD));
            addProduct(new Product("P106", "Ergonomic Desk Chair", 8999.00, 10, ProductTaxCategory.LUXURY));
        } catch (Exception ignored) {
        }
    }

    /**
     * Adds a new product to the catalog.
     */
    public synchronized void addProduct(Product product) throws InvalidInputException, DuplicateEntityException {
        if (product == null) {
            throw new InvalidInputException("Product cannot be null.");
        }
        String id = product.getProductId().toUpperCase();
        if (productCatalog.containsKey(id)) {
            throw new DuplicateEntityException("Product with ID '" + id + "' already exists in inventory.");
        }
        productCatalog.put(id, product);
    }

    /**
     * Finds a product by its unique product ID.
     */
    public Product getProductById(String productId) throws ProductNotFoundException {
        if (productId == null || productId.trim().isEmpty()) {
            throw new ProductNotFoundException("Product ID cannot be empty.");
        }
        Product product = productCatalog.get(productId.trim().toUpperCase());
        if (product == null) {
            throw new ProductNotFoundException(productId.trim().toUpperCase(), true);
        }
        return product;
    }

    /**
     * Returns an unmodifiable list of all products in the catalog.
     */
    public List<Product> getAllProducts() {
        return Collections.unmodifiableList(new ArrayList<>(productCatalog.values()));
    }

    /**
     * Records a purchase/restocking event and updates inventory immediately (Rule 2).
     */
    public synchronized void recordPurchase(Purchase purchase) throws InvalidInputException {
        if (purchase == null) {
            throw new InvalidInputException("Purchase transaction cannot be null.");
        }
        purchase.processPurchase();
        purchaseHistory.add(purchase);
    }

    public List<Purchase> getPurchaseHistory() {
        return Collections.unmodifiableList(purchaseHistory);
    }

    /**
     * Displays a clean formatted table of current inventory products.
     */
    public void displayProductsTable() {
        System.out.println("\n" + "=".repeat(92));
        System.out.println("                               CURRENT INVENTORY PRODUCTS                               ");
        System.out.println("=".repeat(92));
        System.out.printf("%-8s | %-26s | %-12s | %-10s | %-22s\n", 
                "Prod ID", "Product Name", "Price (₹)", "Stock Qty", "Tax Category & Rate");
        System.out.println("-".repeat(92));
        if (productCatalog.isEmpty()) {
            System.out.println("No products currently in stock.");
        } else {
            for (Product p : productCatalog.values()) {
                System.out.printf("%-8s | %-26s | ₹%-11.2f | %-10d | %-22s\n",
                        p.getProductId(),
                        p.getName(),
                        p.getPrice(),
                        p.getStockQuantity(),
                        p.getTaxCategory().toString());
            }
        }
        System.out.println("=".repeat(92));
    }
}
