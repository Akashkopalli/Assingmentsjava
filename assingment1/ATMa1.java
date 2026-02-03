package assingment1;

import java.util.Scanner;
import java.util.InputMismatchException;

// 1. The Model: Manages data and business logic
class BankAccount {
    private double balance;

    public BankAccount(double initialBalance) {
        this.balance = initialBalance;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.printf("Success! You deposited $%.2f%n", amount);
        } else {
            System.out.println("Error: Deposit amount must be positive.");
        }
    }

    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Error: Withdrawal amount must be positive.");
            return false;
        }
        if (amount > balance) {
            System.out.println("Error: Insufficient funds.");
            return false;
        }
        
        balance -= amount;
        System.out.printf("Success! You withdrew $%.2f%n", amount);
        return true;
    }
}

// 2. The Controller/View: Manages user interaction
class ATM {
    private BankAccount account;
    private Scanner scanner;

    public ATM(double startBalance) {
        this.account = new BankAccount(startBalance);
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean isRunning = true;

        while (isRunning) {
            printMenu();
            int choice = getValidIntegerInput();

            switch (choice) {
                case 1:
                    checkBalance();
                    break;
                case 2:
                    handleWithdrawal();
                    break;
                case 3:
                    handleDeposit();
                    break;
                case 4:
                    System.out.println("Thank you for using the ATM. Goodbye!");
                    isRunning = false;
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1-4.");
            }
            System.out.println(); // Empty line for spacing
        }
        scanner.close();
    }

    private void printMenu() {
        System.out.println("=== ATM Menu ===");
        System.out.println("1. Check Balance");
        System.out.println("2. Withdraw Money");
        System.out.println("3. Deposit Money");
        System.out.println("4. Exit");
        System.out.print("Choose an option: ");
    }

    private void checkBalance() {
        System.out.printf("Current Balance: $%.2f%n", account.getBalance());
    }

    private void handleWithdrawal() {
        System.out.print("Enter amount to withdraw: $");
        double amount = getValidDoubleInput();
        if (amount != -1) { // -1 indicates invalid input handling
            account.withdraw(amount);
            System.out.printf("New Balance: $%.2f%n", account.getBalance());
        }
    }

    private void handleDeposit() {
        System.out.print("Enter amount to deposit: $");
        double amount = getValidDoubleInput();
        if (amount != -1) {
            account.deposit(amount);
            System.out.printf("New Balance: $%.2f%n", account.getBalance());
        }
    }

    // Helper method to ensure user types a number
    private double getValidDoubleInput() {
        try {
            return scanner.nextDouble();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input! Please enter a numeric value.");
            scanner.next(); // Clear the invalid input from buffer
            return -1;
        }
    }

    // Helper method to ensure user types an integer for the menu
    private int getValidIntegerInput() {
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            scanner.next(); // Clear invalid input
            return -1; // Return a value that will trigger the default case
        }
    }
}

// 3. The Entry Point
public class ATMa1 {
    public static void main(String[] args) {
        // Initialize ATM with $1000.00
        ATM myAtm = new ATM(1000.00); 
        myAtm.start();
    }
}