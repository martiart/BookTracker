package org.example;
import com.mysql.cj.x.protobuf.MysqlxPrepare;

import java.awt.print.Book;
import java.sql.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String query = "";
        ResultSet resultSet = null;

        // Database connection details----------------------------------------------------------------
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
            // WELCOME USER TO APP--------------------------------------------------------------------------------------
            System.out.println("Welcome to Book Tracker by Art!");
            String userChoice;
            int userId;
            boolean proceed = true;

                while (true) {
                    // Validate that user has an account; if not ask if they would like to create one---------------------------
                    System.out.print("Please enter your name: ");
                    String userName = scanner.nextLine();

                    // Call getReadId to verify if name in database
                    userId = readerHandler.getReaderId(userName);

                    // Name is in database continue to the rest of the application
                    if (userId != -1) {
                        System.out.println("Welcome back " + userName + "!");
                        System.out.println();
                        break;
                    // Name not found. Verify next steps, try again, add name, or exit app
                    } else{
                    System.out.println("User name was not found. Press ENTER to try again. Enter A to Add account. Enter X to exit application.");
                    userChoice = scanner.nextLine();

                    // User wants to try and type name in again
                    if (userChoice.equalsIgnoreCase("")){
                        continue;
                    } // User wants to add name to database
                    else if (userChoice.equalsIgnoreCase("A")) {
                        readerHandler.addReader(userName);
                        System.out.println("Account created successfully. Welcome, " + userName + "!");
                        break;
                    } // User wants to exit the application; we update proceed to exit main loop
                    else if (userChoice.equalsIgnoreCase("X")) {
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
                    System.out.println("What can I do for you today? (Enter Number)");
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

                        // Job duty options which will be handled by BookHandler
                        switch (toDo) {
                            // Add Book
                            case 1:
                                System.out.println("You want to add a book. First I will need several things.");
                                System.out.print("What is the title of the book? ");
                                String title = scanner.nextLine();

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
                                System.out.println("You chose to edit a book's status. What is the title of the book?");
                                String bookTitle = scanner.nextLine();
                                boolean bookFound = true;
                                int attemptCount = 0;

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
                                if (!bookFound) {
                                    System.out.println("You attempted to enter the book 3 times and it was not found. I recommend add the book.");
                                    System.out.println("Exiting to Main Menu...");
                                    break;
                                }
                                // Retrieve current status based on the name which IS in the tracker
//                                String selectQuery = "SELECT read_or_not FROM books WHERE title = ?";
//                                PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
//                                selectStatement.setString(1, bookTitle);
//                                resultSet = selectStatement.executeQuery();

                                String currentStatus = bookHandler.getBookStatus(bookTitle);
                                // Ternary Conditional
                                String currentStatusMeaning = currentStatus.equalsIgnoreCase("N") ? "Not Read" : "Has Been Read";
                                System.out.println("Current status for the book: " + currentStatusMeaning);

                                String newStatus;
                                String newStatusMeaning;
                                while (true) {
                                    System.out.println("What is the new status of the book? (Enter Y for read and N for not read)");
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

//                                query = "UPDATE books SET read_or_not = ? WHERE title = ?";
//                                PreparedStatement updateStatement = connection.prepareStatement(query);
//                                updateStatement.setString(1, newStatus);
//                                updateStatement.setString(2, bookTitle);
//
//                                rowsAffected = updateStatement.executeUpdate();

                                int rowsAffected = bookHandler.updateBookStatus(newStatus, bookTitle);
                                if (rowsAffected > 0) {
                                    System.out.println(bookTitle + " status was successfully updated to: " + newStatusMeaning);
                                } else {
                                    System.out.println("Failed to update book status.");
                                }
                                break;

//                            case 3:
//                                System.out.println("You want all the books you have read. Just a moment while I gather the information.");
//
//                                selectQuery = "SELECT title, author, digital_or_physical FROM books WHERE fk_books_idreader = ? AND read_or_not = 'Y' ORDER BY title ASC ";
//                                selectStatement = connection.prepareStatement(selectQuery);
//                                selectStatement.setInt(1, userId);
//
//                                resultSet = selectStatement.executeQuery();
//                                System.out.printf("%-40s %-29s %-10s%n", "-----TITLE-----", "-----AUTHOR-----", "-----FORMAT-----");
//
//                                while (resultSet.next()) {
//                                    title = resultSet.getString("title");
//                                    author = resultSet.getString("author");
//                                    String format = resultSet.getString("digital_or_physical");
//
//                                    if (format.equalsIgnoreCase("D")) {
//                                        format = "Digital";
//                                    } else {
//                                        format = "Physical";
//                                    }
//                                    System.out.printf("%-40s %-34s %-10s%n", title, author, format);
//
//                                }
//                                System.out.println();
//
//                                break;
//                            case 4:
//                                System.out.println("You want all the books you have NOT read. Just a moment while I gather the information.");
//
//                                selectQuery = "SELECT title, author, digital_or_physical FROM books WHERE fk_books_idreader = ? AND read_or_not = 'N' ORDER BY title ASC ";
//                                selectStatement = connection.prepareStatement(selectQuery);
//                                selectStatement.setInt(1, userId);
//
//                                resultSet = selectStatement.executeQuery();
//                                System.out.printf("%-40s %-29s %-10s%n", "-----TITLE-----", "-----AUTHOR-----", "-----FORMAT-----");
//
//                                if (!resultSet.next()) {
//                                    System.out.println("You do not have any unread books in the tracker!");
//                                } else {
//                                    do {
//                                        // Process each row of the result set here
//                                        title = resultSet.getString("title");
//                                        author = resultSet.getString("author");
//                                        String format = resultSet.getString("digital_or_physical");
//
//                                        // Example: Print out the details
//                                        System.out.printf("%-40s %-40s %-15s%n", title, author, format);
//
//                                    } while (resultSet.next());
//                                }
//
//                                System.out.println();
//
//                                break;
//                            case 5:
//                                System.out.println("Thank you for using Book Tracker by Art. Have a great day!");
//                                return;
//                            default:
//                                System.out.println("Invalid Number. Choose a number between 1 and 5.");
//                                break;
                        }
                        System.out.println("Is there anything else I can do for you?");
                    }

//                connection.close();
        }
        } catch (Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
}
