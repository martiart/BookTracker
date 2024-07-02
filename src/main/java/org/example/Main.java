package org.example;
import java.sql.*;
import java.util.*;

/**
 * Main class for the Book Tracker application.
 */
public class Main {
    /**
     * Main method to start the Book Tracker application.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String query = "";
        ResultSet resultSet = null;

        // Database connection details
        String url = DBConfig.getUrl();
        String username = DBConfig.getUsername();
        String password = DBConfig.getPassword();

        try {
            // Load MySql JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish database connection
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();

            // Initialize handlers
            ReaderHandler readerHandler = new ReaderHandler(connection);
            BookHandler bookHandler = new BookHandler(connection);

            while (true) {
                // WELCOME USER TO APP
                System.out.println("Welcome to Book Tracker by Art!");
                System.out.println();
                String userChoice;
                int userId;
                boolean proceed = true;
                String userName;

                while (true) {
                    // Prompt for name
                    System.out.print("Please enter your name: ");
                    userName = scanner.nextLine();

                    // Call getReadId to verify if name in database
                    userId = readerHandler.getReaderId(userName);

                    // Name is in database continue to the rest of the application
                    if (userId != -1) {
                        System.out.println("Welcome back " + userName + "!");
                        System.out.println();
                        break;

                    } else{
                        // Name not found. Verify next steps, try again, add name, or exit app
                        System.out.println("User name was not found. Press ENTER to try again. Enter A to Add account. Enter X to exit application.");
                        userChoice = scanner.nextLine();

                        // User wants to try and type name in again
                        if (userChoice.equalsIgnoreCase("")){
                            continue;
                        }
                        else if (userChoice.equalsIgnoreCase("A")) {
                            // User wants to add name to database
                            readerHandler.addReader(userName);
                            System.out.println("Account created successfully. Welcome, " + userName + "!");
                            break;
                        }
                        else if (userChoice.equalsIgnoreCase("X")) {
                            // User wants to exit the application; we update proceed to exit main loop
                            System.out.println("Thank you for using Book Tracker by Art. Goodbye!");
                            proceed = false;
                            break;
                        }
                    }
                }

                // Check if we need to exit the main loop
                if (!proceed) {
                    break;
                }
                System.out.println(userName + ", what can I do for you? (Enter Number)");
                // User is valid and wants to continue into the main application functionality
                while (true) {
                    // Menu options of actions that can be performed
                    System.out.println("1. Add Book");
                    System.out.println("2. Edit Book Status");
                    System.out.println("3. Get Read Books");
                    System.out.println("4. Get Unread Books");
                    System.out.println("5. EXIT APPLICATION");

                    System.out.print("Number Choice: ");
                    int toDo = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println();

                        // Job duty options which will be handled by BookHandler
                    switch (toDo) {
                        case 1:
                            // Add Book
                            System.out.println("You want to add a book. First I will need several things.");
                            System.out.print("What is the title of the book? ");
                            String title = scanner.nextLine();

                            // CHecks if book already exists
                            boolean alreadyExists = bookHandler.isBook(title);
                            if (alreadyExists){
                                System.out.println(title + " --- Already exists in your collection");
                                System.out.println("Consider editing the book status. Exiting to Main Menu");
                                break;
                            }

                            // Get book information
                            System.out.print("Who is the author? ");
                            String author = scanner.nextLine();

                            System.out.print("Have you read it yet (Y or N)? ");
                            String read_or_not = scanner.nextLine();

                            System.out.print("Was it a physical or digital book (P or D)? ");
                            String digital_or_physical = scanner.nextLine();

                            // Call book handler to add book
                            bookHandler.addBook(title, author, userId, read_or_not, digital_or_physical);

                            System.out.println("Book added successfully!");
                            break;
                        case 2:
                            // Edit book status
                            System.out.print("You chose to edit a book's status. What is the title of the book?");
                            String bookTitle = scanner.nextLine();
                            boolean bookFound = true;
                            int attemptCount = 0;

                            // Give tries to enter correct input
                            while (attemptCount < 2) { // Allow up to 3 attempts
                                bookFound = bookHandler.isBook(bookTitle);

                                if (bookFound) {
                                    System.out.println("Book is in the tracker. Let's update it.");
                                    break;
                                } else {
                                    System.out.println("Book was not found. Try again. What is the title of the book?");
                                    bookTitle = scanner.nextLine();
                                    attemptCount++;
                                }
                            }
                            // to many failed attempts return to main menu
                            if (!bookFound) {
                                System.out.println("You attempted to enter the book 3 times and it was not found. I recommend add the book.");
                                System.out.println("Exiting to Main Menu...");
                                break;
                            }

                            String currentStatus = bookHandler.getBookStatus(bookTitle);
                            // Ternary Conditional
                            String currentStatusMeaning = currentStatus.equalsIgnoreCase("N") ? "Not Read" : "Has Been Read";
                            System.out.println("Current status for the book: " + currentStatusMeaning);

                            String newStatus;
                            String newStatusMeaning;
                            // Set status
                            while (true) {
                                System.out.print("What is the new status of the book (Enter Y for read and N for not read)? ");
                                newStatus = scanner.nextLine().toUpperCase();
                                if (newStatus.equalsIgnoreCase("Y")){
                                    newStatusMeaning = "Has Been Read";
                                    break;
                                } else if (newStatus.equalsIgnoreCase("N")) {
                                    newStatusMeaning = "Has NOT been Read";
                                    break;
                                } else {
                                    System.out.println("Invalid input. Please enter Y for read or N for not read.");
                                }
                            }
                            // Verify change occured
                            int rowsAffected = bookHandler.updateBookStatus(newStatus, bookTitle);
                            if (rowsAffected > 0) {
                                System.out.println(bookTitle + " status was successfully updated to: " + newStatusMeaning);
                            } else {
                                System.out.println("Failed to update book status.");
                            }
                            break;

                        case 3:
                            // Get READ book printed
                            System.out.println("You want all the books you have read. Just a moment while I gather the information.");
                            System.out.println();
                            String choice = "Y";

                            // Call to print READ books
                            bookHandler.getBooks(userId, choice);
                            break;
                        case 4:
                            // Call to print UNREAD books
                            System.out.println("You want all the books you have NOT read. Just a moment while I gather the information.");
                            System.out.println();
                            choice = "N";

                            // Call to print UNREAD books
                            bookHandler.getBooks(userId, choice);
                            break;
                        case 5:
                            // Exit application
                            System.out.println("Thank you for using Book Tracker by Art. Have a great day!");
                            return;
                        default:
                            System.out.println("Invalid Number. Choose a number between 1 and 5.");
                            break;
                    }
                    System.out.println();
                    System.out.println("Is there anything else I can do for you?");
                }
            }
        } catch (Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
}
