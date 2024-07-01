package org.example;
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

            while (true){
            // WELCOME USER TO APP--------------------------------------------------------------------------------------
            System.out.println("Welcome to Book Tracker by Art!");
            System.out.println();

            // Validate that user has an account; if not ask if they would like to create one---------------------------
            System.out.print("Please enter your name: ");
            String userName = scanner.nextLine();


            query = "SELECT * FROM readers WHERE readername = '" + userName + "'";
            resultSet = statement.executeQuery(query);

            String userChoice;
            if (resultSet.next()) {
                System.out.println("Welcome back " + userName + "!");
                System.out.println();
            } else {
                System.out.println("User name was not found. Would you like to create an account? Y to create account or N to exit application");
                while (true) {
                    userChoice = scanner.nextLine();

                    if (userChoice.equalsIgnoreCase("Y")) {
                        query = "INSERT INTO readers (readername) VALUES ('" + userName + "')";
                        statement.executeUpdate(query);
                        System.out.println("Account created successfully. Welcome, " + userName + "!");
                        break;
                    } else if (userChoice.equalsIgnoreCase("N")) {
                        break;
                    } else {
                        System.out.println("Enter a valid choice. Y to create account or N to exit application");
                    }
                }
                if (userChoice.equalsIgnoreCase("N")){
                    System.out.println("Thank you for using Book Tracker by Art. Goodbye!");
                    break;
                }
            }
                System.out.println("What can I do for you today? (Enter Number)");

                while (true){
                    System.out.println("1. Add Book");
                    System.out.println("2. Edit Book Status");
                    System.out.println("3. Get Read Books");
                    System.out.println("4. Get Unread Books");
                    System.out.println("5. EXIT APPLICATION");

                    int toDo = scanner.nextInt();
                    scanner.nextLine();
                    switch (toDo){
                        case 1:
                            System.out.println("You want to add a book. First I will need several things.");

                            System.out.print("What is the title of the book? ");
                            String title = scanner.nextLine();

                            System.out.print("Who is the author? ");
                            String author = scanner.nextLine();

                            System.out.print("Have you read it yet (Y or N)? ");
                            String read_or_not = scanner.nextLine();

                            System.out.print("Was it a physical or digital book (D or G)? ");
                            String digital_or_physical = scanner.nextLine();

                            // Retrieve user id based on name which IS in tracker
                            String selectQuery = "SELECT idreader FROM readers WHERE readername = ?";
                            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
                            selectStatement.setString(1, userName);
                            int user_id = resultSet.getInt("idreader");

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
                            if (rowsAffected > 0){
                                System.out.println("Book added successfully!");
                            } else{
                                System.out.println("Failed to add book!");
                            }

                            break;
                        case 2:
                            System.out.println("You chose to edit a books status. What is the title of the book?");
                            String bookTitle = scanner.nextLine();
//                            String userChoice;
                            int i = 0;
                            while (i < 2) {
                                query = "SELECT * FROM books WHERE title = '" + bookTitle + "'";
                                resultSet = statement.executeQuery(query);

                                if (resultSet.next()) {
                                    System.out.println("Book is in the tracker. Let's update it.");
                                    System.out.println();

                                    // Retrieve current status based on name of book which IS in tracker
                                    query = "SELECT read_or_not FROM books WHERE title = '" + bookTitle + "'";
                                    resultSet = statement.executeQuery(query);

                                    break;
                                } else {
                                    System.out.println("Book was not found. Try again. What is the title of the book? ");
                                    bookTitle = scanner.nextLine();
                                    i++;
                                }
                                System.out.println("You attempted to enter book 3 times and it was not found. I suggest Adding the Book.");
                                System.out.println();
                                }

                            break;
                        case 3:
                            break;
                        case 4:
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

        }

//            resultSet = statement.executeQuery("select * from books");
//            System.out.printf("%-40s %-40s%n", "Title", "Author");
//
//            while (resultSet.next()){
//                String author = resultSet.getString("author");
//                String title = resultSet.getString("title");
//
//                System.out.printf("%-40s %-40s%n", title, author);
//            }
            connection.close();
        } catch (Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
}
