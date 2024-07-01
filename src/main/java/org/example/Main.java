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


        try{
            // Load MySql JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish database connection
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();


            // WELCOME USER TO APP--------------------------------------------------------------------------------------
            System.out.println("Welcome to Art's Book Tracking App!");
            System.out.println();

            // Validate that user has an account; if not ask if they would like to create one---------------------------
            System.out.print("Please enter your name: ");
            String userName = scanner.nextLine();

            query = "SELECT * FROM readers WHERE readername = '" + userName + "'";
            resultSet = statement.executeQuery(query);

            while (true) {
                if (resultSet.next()) {
                    System.out.println("Welcome back " + userName + "!");
                    break;
                } else {
                    System.out.println("User name was not found. Would you like to create an account? Y or N");
                    String userChoice = scanner.nextLine();

                    if (userChoice.equalsIgnoreCase("Y")){
                        query = "INSERT INTO readers (readername) VALUES ('"+ userName +"')";
                        statement.executeUpdate(query);
                        System.out.println("Account created successfully. Welcome, " + userName + "!");
                        break;
                    }
                }
            }

            System.out.println("Hey " + userName + "! What can I do for you today?");
            System.out.println();

            resultSet = statement.executeQuery("select * from books");

            System.out.printf("%-40s %-40s%n", "Title", "Author");

            while (resultSet.next()){
                String author = resultSet.getString("author");
                String title = resultSet.getString("title");

                System.out.printf("%-40s %-40s%n", title, author);
            }
            connection.close();
        } catch (Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
}
