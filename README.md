# 23CSR306 JAVA PROGRAMMING - ASSIGNMENT II: OOP MINI PROJECT

## Academic Year: 2026-2027 | Department of Computer Science and Engineering
### Karpagam College of Engineering (Autonomous), Coimbatore - 641032

---

### Student Details
- **Student Name:** MEGA VARSAN VE
- **Roll Number:** 717825I231
- **Register Number:** 717825I231
- **Year / Semester / Section:** II / III / B
- **Department:** Artificial Intelligence and Data Science (AD)
- **Course Code & Title:** 23CSR306 - Java Programming
- **Date of Submission:** 25-10-2026

- **Repository URL:** `https://github.com/tcgmegavarsan-sys/Java-Programming-Mini-Project-Assignment-II`

### 1.1 Problem Statement
Design and implement a Java console application for an **Inventory and Billing System** that handles product purchases, sales, customer types, and a reward points system for premium customers. The application demonstrates core object-oriented programming principles (**encapsulation**, **inheritance**, **aggregation/composition**, and **polymorphism**) and maintains accurate, atomic stock quantities across all operations.

### 1.2 Class Requirements
- `Product`
- `ProductTaxCategory`
- `Customer`
- `PremiumCustomer`
- `SaleDetail`
- `Sale`
- `PurchaseDetail`
- `Purchase`

*(Additional helper classes implemented: `InventoryService`, `BillingService`, `InputValidator`, custom exceptions `InsufficientStockException`, `ProductNotFoundException`, `CustomerNotFoundException`, `DuplicateEntityException`, `InvalidInputException`, and main runner `InventoryBillingApp`)*

### 1.3 Business Rules Implemented
1. **Stock Verification Prior to Sale Confirmation**: Available stock is verified before finalizing any sale. If requested quantity exceeds inventory, the transaction aborts with an `InsufficientStockException` and no items are deducted.
2. **Immediate Stock Quantity Updates**: Stock quantities are updated atomically and immediately following a confirmed sale (deduction) or supplier purchase (increment).
3. **Reward Points System for Premium Customers**:
   - Premium customers earn **1 reward point for every ₹100 spent** (calculated on amount after discounts, before tax).
   - When accumulated points reach **200 or more**, a loyalty discount of **₹10 per 100 points** is automatically applied to the invoice, and the redeemed points are deducted from the customer's balance.
4. **Regular Customer Treatment**: Regular customers do not earn reward points and do not receive loyalty points discounts.
5. **Direct Product Linkage**: Every `SaleDetail` and `PurchaseDetail` directly references its associated `Product` object.

### 1.4 Menu Options
1. `Add Product`
2. `Add Customer`
3. `Make a Purchase`
4. `Make a Sale`
5. `Display Products`
6. `Exit`

---

## 2. Object-Oriented Programming (OOP) Architecture

| OOP Principle | Implementation Details in Codebase |
| :--- | :--- |
| **Encapsulation** | All entity fields (`Product`, `Customer`, `Sale`, `Purchase`, etc.) are declared `private` with explicit, validated public getters and setters. Data mutations are guarded with domain constraints (e.g., negative prices/quantities rejected). |
| **Inheritance** | `PremiumCustomer` extends `Customer`, reusing foundational profile attributes (`customerId`, `name`, `contactNumber`, `email`) while introducing specialized attributes (`rewardPoints`, `membershipTier`). |
| **Polymorphism** | `Customer` defines polymorphic methods `calculateDiscount(double spentBeforeTax)`, `calculatePointsEarned(double amount)`, and `redeemPointsForDiscount(double discount)`. `PremiumCustomer` overrides these to implement the ₹10/100 pts discount rule and 1 pt/₹100 accrual rule. Dynamic method dispatch handles billing seamlessly without `instanceof` branching. |
| **Aggregation** | `SaleDetail` and `PurchaseDetail` maintain direct references to `Product` instances, reflecting independent product existence. `Sale` aggregates `Customer`. |
| **Composition** | `Sale` manages the lifecycle of its `SaleDetail` items; `Purchase` manages its `PurchaseDetail` items. |
| **Exception Handling** | Custom checked exceptions (`InsufficientStockException`, `ProductNotFoundException`, etc.) and `InputValidator` prevent runtime crashes on invalid inputs. |

---

## 3. Package Structure

```
c:\Users\tcgme\Downloads\java proj\
├── .classpath                               # Eclipse IDE classpath configuration
├── .project                                 # Eclipse IDE project definition
├── README.md                                # Project documentation and guide
├── src\
│   └── com\
│       └── inventory\
│           ├── model\
│           │   ├── ProductTaxCategory.java  # GST tax category definitions & rates
│           │   ├── Product.java             # Product entity & stock mutators
│           │   ├── Customer.java            # Base customer class (Regular)
│           │   ├── PremiumCustomer.java     # Subclass with loyalty points logic
│           │   ├── PurchaseDetail.java      # Line item for supplier restock
│           │   ├── Purchase.java            # Supplier purchase order transaction
│           │   ├── SaleDetail.java          # Line item for sales invoice
│           │   └── Sale.java                # Sale invoice, billing & points processor
│           ├── exception\
│           │   ├── InsufficientStockException.java
│           │   ├── ProductNotFoundException.java
│           │   ├── CustomerNotFoundException.java
│           │   ├── DuplicateEntityException.java
│           │   └── InvalidInputException.java
│           ├── service\
│           │   ├── InventoryService.java    # Product catalog & inventory management
│           │   └── BillingService.java      # Customer registry & invoice operations
│           ├── util\
│           │   └── InputValidator.java      # Scanner input validation & error recovery
│           └── main\
│               └── InventoryBillingApp.java # Interactive console menu & CLI controller
└── bin\                                     # Compiled bytecode (.class files)
```

---

## 4. How to Compile and Run

### Method 1: Using Command Line / Terminal
1. Ensure Java JDK 11 or higher (e.g. Eclipse Temurin 17 / Microsoft JDK 21) is installed.
2. Open terminal in the project directory:
   ```bash
   cd "c:\Users\tcgme\Downloads\java proj"
   ```
3. Compile all source files into the `bin` directory:
   ```bash
   javac -d bin src/com/inventory/*/*.java
   ```
4. Run the main application:
   ```bash
   java -cp bin com.inventory.main.InventoryBillingApp
   ```

### Method 2: In Eclipse IDE
1. Open Eclipse IDE.
2. Select **File -> Import... -> General -> Existing Projects into Workspace**.
3. Browse and select the project directory: `c:\Users\tcgme\Downloads\java proj`.
4. Click **Finish**.
5. Locate `src/com/inventory/main/InventoryBillingApp.java`.
6. Right-click and choose **Run As -> Java Application**.

---

## 5. Sample Execution Scenarios

### Scenario A: Display Available Inventory
Displays all initial stocked items with their unit price, available stock quantity, and configured GST category.

### Scenario B: Restocking Products (Make a Purchase)
A purchase transaction of 10 units for `P101` (Wireless Gaming Mouse) at ₹950/unit is processed from supplier "Wholesale Tech Supplies". Stock automatically and immediately updates from 30 to 40 units.

### Scenario C: Sale to Regular Customer
Rajesh Sharma (`C101`) purchases 2 units of `P104` (Organic Green Tea) at ₹350/unit.
- Gross Total: ₹700.00
- Discount: ₹0.00 (Regular customer)
- GST (5%): ₹35.00
- Net Payable: ₹735.00
- Reward Points Earned: 0 pts
- Product stock immediately updates from 50 to 48 units.

### Scenario D: Sale to Premium Customer with Points Redemption
Priya Sundaram (`C102`, 250 points) purchases 1 `P102` (Mechanical Keyboard) and 2 `P101` (Wireless Gaming Mouse).
- Gross Total: ₹6,497.00
- Points Redeemed: 200 points = ₹20.00 discount applied.
- Subtotal after discount: ₹6,477.00
- GST (18%): ₹1,165.86
- Net Payable: ₹7,642.86
- Points Earned: +64 points (1 pt per ₹100 spent)
- Updated Reward Points: `(250 - 200) + 64 = 114 points`.
- Product stock immediately updates: `P102` (20 -> 19), `P101` (40 -> 38).

### Scenario E: Business Rule 1 Stock Shortage Validation
Attempting to purchase 99 units of `P106` (Ergonomic Desk Chair) when only 10 units are available. The system detects stock deficit, warns the user, and aborts without decrementing inventory.
