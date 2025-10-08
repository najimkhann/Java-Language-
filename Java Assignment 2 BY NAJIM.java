
// Najim Khan
// Roll No: 2401420003
// B.Tech CSE (DS)
// Assignment 2 - Calculator Application Using Method Overloading
// Java Programming (ENCS201)

import java.util.Scanner;

// Calculator class with overloaded methods
class Calculator {

    // Addition methods
    int add(int a, int b) {
        return a + b;
    }

    double add(double a, double b) {
        return a + b;
    }

    int add(int a, int b, int c) {
        return a + b + c;
    }

    // Subtraction method
    int subtract(int a, int b) {
        return a - b;
    }

    // Multiplication method
    double multiply(double a, double b) {
        return a * b;
    }

    // Division method with divide-by-zero handling
    double divide(int a, int b) {
        if (b == 0) {
            System.out.println("Error: Division by zero is not allowed.");
            return 0;
        }
        return (double) a / b;
    }
}

// UserInterface class for menu-driven interaction
public class UserInterface {
    Scanner sc = new Scanner(System.in);
    Calculator calc = new Calculator();

    // Perform addition with user input
    void performAddition() {
        System.out.println("Enter 2 or 3 numbers to add? (2/3): ");
        int choice = sc.nextInt();
        if (choice == 2) {
            System.out.print("Enter first number: ");
            int a = sc.nextInt();
            System.out.print("Enter second number: ");
            int b = sc.nextInt();
            System.out.println("Result: " + calc.add(a, b));
        } else if (choice == 3) {
            System.out.print("Enter first number: ");
            int a = sc.nextInt();
            System.out.print("Enter second number: ");
            int b = sc.nextInt();
            System.out.print("Enter third number: ");
            int c = sc.nextInt();
            System.out.println("Result: " + calc.add(a, b, c));
        } else {
            System.out.println("Invalid choice for addition.");
        }
    }

    // Perform subtraction
    void performSubtraction() {
        System.out.print("Enter first number: ");
        int a = sc.nextInt();
        System.out.print("Enter second number: ");
        int b = sc.nextInt();
        System.out.println("Result: " + calc.subtract(a, b));
    }

    // Perform multiplication
    void performMultiplication() {
        System.out.print("Enter first number: ");
        double a = sc.nextDouble();
        System.out.print("Enter second number: ");
        double b = sc.nextDouble();
        System.out.println("Result: " + calc.multiply(a, b));
    }

    // Perform division
    void performDivision() {
        System.out.print("Enter numerator: ");
        int a = sc.nextInt();
        System.out.print("Enter denominator: ");
        int b = sc.nextInt();
        System.out.println("Result: " + calc.divide(a, b));
    }

    // Main menu
    void mainMenu() {
        int choice;
        do {
            System.out.println("\nWelcome to the Calculator Application!");
            System.out.println("1. Add Numbers");
            System.out.println("2. Subtract Numbers");
            System.out.println("3. Multiply Numbers");
            System.out.println("4. Divide Numbers");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    performAddition();
                    break;
                case 2:
                    performSubtraction();
                    break;
                case 3:
                    performMultiplication();
                    break;
                case 4:
                    performDivision();
                    break;
                case 5:
                    System.out.println("Exiting... Thank you!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 5);
    }

    // Main method
    public static void main(String[] args) {
        UserInterface ui = new UserInterface();
        ui.mainMenu();
    }
}
