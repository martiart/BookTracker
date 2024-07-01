package org.example;
import com.mysql.cj.x.protobuf.MysqlxPrepare;

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

        // Initialize handlers



        try {
            // Load MySql JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish database connection
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();

            while (true) {
                // WELCOME USER TO APP--------------------------------------------------------------------------------------
                System.out.println("Welcome to Book Tracker by Art!");
                System.out.println();

                // Validate that user has an account; if not ask if they would like to create one---------------------------
                System.out.print("Please enter your name: ");
                String userName = scanner.nextLine();

                query = "SELECT * FROM readers WHERE readername = '" + userName + "'";
                resultSet = statement.executeQuery(query);

                String userChoice;
                int user_id = -1;
                if (resultSet.next()) {
                    user_id = resultSet.getInt("idreader");
                    System.out.println("Welcome back " + userName + "!");
                    System.out.println();
                } else {
                    System.out.println("User name was not found. Would you like to create an account? Y to create account or N to exit application");
                    while (true) {
                        userChoice = scanner.nextLine();

                        if (userChoice.equalsIgnoreCase("Y")) {
                            query = "INSERT INTO readers (readername) VALUES (?)";
                            PreparedStatement insertStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                            insertStatement.setString(1, userName);
                            insertStatement.executeUpdate();
                            resultSet = insertStatement.getGeneratedKeys();
                            if (resultSet.next()) {
                                user_id = resultSet.getInt(1);
                            }
                            System.out.println("Account created successfully. Welcome, " + userName + "!");
                        } else {
                            System.out.println("Thank you for using Book Tracker by Art. Goodbye!");
                            break;
                        }
                    }
                }

                System.out.println("What can I do for you today? (Enter Number)");

                while (true) {
                    System.out.println("1. Add Book");
                    System.out.println("2. Edit Book Status");
                    System.out.println("3. Get Read Books");
                    System.out.println("4. Get Unread Books");
                    System.out.println("5. EXIT APPLICATION");

                    int toDo = scanner.nextInt();
                    scanner.nextLine();

                    switch (toDo) {
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


                            // Populate query to insert new book
                            String insertQuery = "INSERT INTO books (title, author, fk_books_idreader, read_or_not, digital_or_physical) " + "VALUES  (?, ?, ?, ?, ?)"; // ('"+ title + "', '" + author + "', '" + user_id + "', '" + read_or_not + "', '" + digital_or_physical + "')";
                            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                            insertStatement.setString(1, title);
                            insertStatement.setString(2, author);
                            insertStatement.setInt(3, user_id);
                            insertStatement.setString(4, read_or_not);
                            insertStatement.setString(5, digital_or_physical);

                            // Verify change occured, if rows affected >= 1 added; else not added
                            int rowsAffected = insertStatement.executeUpdate();
                            if (rowsAffected > 0) {
                                System.out.println("Book added successfully!");
                            } else {
                                System.out.println("Failed to add book!");
                            }
                            insertStatement.close();
                            break;
                        case 2:
                            System.out.println("You chose to edit a book's status. What is the title of the book?");
                            String bookTitle = scanner.nextLine();
                            boolean bookFound = false;
                            int attemptCount = 0;

                            while (attemptCount < 3) { // Allow up to 3 attempts
                                query = "SELECT * FROM books WHERE title = ?";
                                PreparedStatement preparedStatement = connection.prepareStatement(query);
                                preparedStatement.setString(1, bookTitle);
                                resultSet = preparedStatement.executeQuery();

                                if (resultSet.next()) {
                                    System.out.println("Book is in the tracker. Let's update it.");
                                    bookFound = true;
                                    break;
                                } else {
                                    System.out.println("Book was not found. Try again. What is the title of the book?");
                                    bookTitle = scanner.nextLine();
                                    attemptCount++;
                                }

                                preparedStatement.close();
                            }

                            if (!bookFound) {
                                System.out.println("You attempted to enter the book 3 times and it was not found. Exiting to Main Menu.");
                                System.out.println("I recommend adding the book.");
                                break;
                            }

                            // Retrieve current status based on the name which IS in the tracker
                            String selectQuery = "SELECT read_or_not FROM books WHERE title = ?";
                            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
                            selectStatement.setString(1, bookTitle);
                            resultSet = selectStatement.executeQuery();

                            if (resultSet.next()) {
                                String currentStatus = resultSet.getString("read_or_not");
                                // Ternary Conditional
                                String currentStatusMeaning = currentStatus.equalsIgnoreCase("N") ? "Not Read" : "Has Been Read";
                                System.out.println("Current status for the book: " + currentStatusMeaning);

                                String newStatus;
                                while (true) {
                                    System.out.println("What is the new status of the book? (Enter Y for read and N for not read)");
                                    newStatus = scanner.nextLine().toUpperCase();
                                    if (newStatus.equals("Y") || newStatus.equals("N")) {
                                        break;
                                    } else {
                                        System.out.println("Invalid input. Please enter Y for read or N for not read.");
                                    }
                                }

                                query = "UPDATE books SET read_or_not = ? WHERE title = ?";
                                PreparedStatement updateStatement = connection.prepareStatement(query);
                                updateStatement.setString(1, newStatus);
                                updateStatement.setString(2, bookTitle);

                                rowsAffected = updateStatement.executeUpdate();
                                if (rowsAffected > 0) {
                                    System.out.println("Book status updated successfully!");
                                } else {
                                    System.out.println("Failed to update book status.");
                                }

                                updateStatement.close();
                            } else {
                                System.out.println("Error retrieving current status.");
                            }

                            selectStatement.close();
                            break;

                        case 3:
                            System.out.println("You want all the books you have read. Just a moment while I gather the information.");

                            selectQuery = "SELECT title, author, digital_or_physical FROM books WHERE fk_books_idreader = ? AND read_or_not = 'Y' ORDER BY title ASC ";
                            selectStatement = connection.prepareStatement(selectQuery);
                            selectStatement.setInt(1, user_id);

                            resultSet = selectStatement.executeQuery();
                            System.out.printf("%-40s %-29s %-10s%n", "-----TITLE-----", "-----AUTHOR-----", "-----FORMAT-----");

                            while (resultSet.next()) {
                                title = resultSet.getString("title");
                                author = resultSet.getString("author");
                                String format = resultSet.getString("digital_or_physical");

                                if (format.equalsIgnoreCase("D")) {
                                    format = "Digital";
                                } else {
                                    format = "Physical";
                                }
                                System.out.printf("%-40s %-34s %-10s%n", title, author, format);

                            }
                            System.out.println();

                            break;
                        case 4:
                            System.out.println("You want all the books you have NOT read. Just a moment while I gather the information.");

                            selectQuery = "SELECT title, author, digital_or_physical FROM books WHERE fk_books_idreader = ? AND read_or_not = 'N' ORDER BY title ASC ";
                            selectStatement = connection.prepareStatement(selectQuery);
                            selectStatement.setInt(1, user_id);

                            resultSet = selectStatement.executeQuery();
                            System.out.printf("%-40s %-29s %-10s%n", "-----TITLE-----", "-----AUTHOR-----", "-----FORMAT-----");

                            if (!resultSet.next()) {
                                System.out.println("You do not have any unread books in the tracker!");
                            } else {
                                do {
                                    // Process each row of the result set here
                                    title = resultSet.getString("title");
                                    author = resultSet.getString("author");
                                    String format = resultSet.getString("digital_or_physical");

                                    // Example: Print out the details
                                    System.out.printf("%-40s %-40s %-15s%n", title, author, format);

                                } while (resultSet.next());
                            }

                            System.out.println();

                            break;
                        case 5:
                            System.out.println("Thank you for using Book Tracker by Art. Have a great day!");
                            return;
                        default:
                            System.out.println("Invalid Number. Choose a number between 1 and 5.");
                            break;
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
