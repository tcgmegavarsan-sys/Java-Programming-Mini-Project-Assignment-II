package com.inventory.service;

import com.inventory.exception.CustomerNotFoundException;
import com.inventory.exception.DuplicateEntityException;
import com.inventory.exception.InsufficientStockException;
import com.inventory.exception.InvalidInputException;
import com.inventory.model.Customer;
import com.inventory.model.PremiumCustomer;
import com.inventory.model.Sale;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service handling customer management, billing transactions, and sales records.
 */
public class BillingService {
    private final Map<String, Customer> customerRegistry = new LinkedHashMap<>();
    private final List<Sale> salesHistory = new ArrayList<>();

    public BillingService() {
        seedInitialCustomers();
    }

    /**
     * Seeds initial regular and premium customers for realistic demonstration.
     */
    private void seedInitialCustomers() {
        try {
            addCustomer(new Customer("C101", "Rajesh Sharma", "9876543210", "rajesh.sharma@example.com"));
            addCustomer(new PremiumCustomer("C102", "Priya Sundaram", "9845123456", "priya.sundaram@example.com", 250));
            addCustomer(new PremiumCustomer("C103", "Anand Kumar", "9765432109", "anand.kumar@example.com", 150));
            addCustomer(new Customer("C104", "Kavitha Ramesh", "9123456780", "kavitha.r@example.com"));
        } catch (Exception ignored) {
        }
    }

    /**
     * Registers a customer into the system.
     */
    public synchronized void addCustomer(Customer customer) throws InvalidInputException, DuplicateEntityException {
        if (customer == null) {
            throw new InvalidInputException("Customer cannot be null.");
        }
        String id = customer.getCustomerId().toUpperCase();
        if (customerRegistry.containsKey(id)) {
            throw new DuplicateEntityException("Customer with ID '" + id + "' already exists.");
        }
        customerRegistry.put(id, customer);
    }

    /**
     * Retrieves a customer by ID.
     */
    public Customer getCustomerById(String customerId) throws CustomerNotFoundException {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new CustomerNotFoundException("Customer ID cannot be empty.");
        }
        Customer customer = customerRegistry.get(customerId.trim().toUpperCase());
        if (customer == null) {
            throw new CustomerNotFoundException(customerId.trim().toUpperCase(), true);
        }
        return customer;
    }

    /**
     * Returns an unmodifiable list of registered customers.
     */
    public List<Customer> getAllCustomers() {
        return Collections.unmodifiableList(new ArrayList<>(customerRegistry.values()));
    }

    /**
     * Processes a sale invoice, validates business rules, and saves to history.
     */
    public synchronized void recordSale(Sale sale) throws InsufficientStockException, InvalidInputException {
        if (sale == null) {
            throw new InvalidInputException("Sale transaction cannot be null.");
        }
        sale.processSale();
        salesHistory.add(sale);
    }

    /**
     * Returns the sales history.
     */
    public List<Sale> getSalesHistory() {
        return Collections.unmodifiableList(salesHistory);
    }

    /**
     * Displays a formatted table of all registered customers.
     */
    public void displayCustomersTable() {
        System.out.println("\n" + "=".repeat(92));
        System.out.println("                               REGISTERED CUSTOMERS                                     ");
        System.out.println("=".repeat(92));
        System.out.printf("%-8s | %-20s | %-12s | %-18s | %-12s\n", 
                "Cust ID", "Customer Name", "Contact No", "Customer Type", "Points Balance");
        System.out.println("-".repeat(92));
        if (customerRegistry.isEmpty()) {
            System.out.println("No customers currently registered.");
        } else {
            for (Customer c : customerRegistry.values()) {
                String pointsStr = (c instanceof PremiumCustomer) ? String.valueOf(c.getRewardPoints()) + " pts" : "N/A (Regular)";
                System.out.printf("%-8s | %-20s | %-12s | %-18s | %-12s\n",
                        c.getCustomerId(),
                        c.getName(),
                        c.getContactNumber(),
                        c.getCustomerType(),
                        pointsStr);
            }
        }
        System.out.println("=".repeat(92));
    }
}
