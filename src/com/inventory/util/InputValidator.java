package com.inventory.util;

import com.inventory.exception.InvalidInputException;
import java.util.Scanner;

/**
 * Robust input reader and validator for console applications.
 * Enforces input validation rules and catches parsing errors without crashing the program.
 */
public class InputValidator {
    private final Scanner scanner;

    public InputValidator(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Reads a non-empty string prompt from the user.
     */
    public String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            if (input != null && !input.trim().isEmpty()) {
                return input.trim();
            }
            System.out.println("  [Validation Error] Input cannot be blank. Please try again.");
        }
    }

    /**
     * Reads an integer within an inclusive range.
     */
    public int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            try {
                if (line == null || line.trim().isEmpty()) {
                    throw new InvalidInputException("Input cannot be empty.");
                }
                int value = Integer.parseInt(line.trim());
                if (value < min || value > max) {
                    throw new InvalidInputException("Value must be between " + min + " and " + max + ".");
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("  [Validation Error] Invalid format! Please enter a valid whole number.");
            } catch (InvalidInputException e) {
                System.out.println("  [Validation Error] " + e.getMessage());
            }
        }
    }

    /**
     * Reads a positive integer (e.g. for quantity).
     */
    public int readPositiveInt(String prompt) {
        return readInt(prompt, 1, Integer.MAX_VALUE);
    }

    /**
     * Reads a non-negative integer (e.g. for initial points).
     */
    public int readNonNegativeInt(String prompt) {
        return readInt(prompt, 0, Integer.MAX_VALUE);
    }

    /**
     * Reads a double value within an inclusive range.
     */
    public double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            try {
                if (line == null || line.trim().isEmpty()) {
                    throw new InvalidInputException("Input cannot be empty.");
                }
                double value = Double.parseDouble(line.trim());
                if (value < min || value > max) {
                    throw new InvalidInputException(String.format("Value must be between %.2f and %.2f.", min, max));
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("  [Validation Error] Invalid number format! Please enter a valid numeric value.");
            } catch (InvalidInputException e) {
                System.out.println("  [Validation Error] " + e.getMessage());
            }
        }
    }

    /**
     * Reads a positive double (e.g. for price).
     */
    public double readPositiveDouble(String prompt) {
        return readDouble(prompt, 0.01, Double.MAX_VALUE);
    }

    /**
     * Reads a yes/no confirmation.
     */
    public boolean readBoolean(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String line = scanner.nextLine();
            if (line != null) {
                String val = line.trim().toLowerCase();
                if (val.equals("y") || val.equals("yes")) return true;
                if (val.equals("n") || val.equals("no")) return false;
            }
            System.out.println("  [Validation Error] Please enter 'y' for Yes or 'n' for No.");
        }
    }
}
