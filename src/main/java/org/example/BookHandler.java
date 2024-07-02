package org.example;
import java.sql.*;

public class BookHandler {
    private Connection connection;

    public BookHandler(Connection connection) {
        this.connection = connection;
    }

    public void addBook(String title, String author, int userId, String readOrNot, String digitalOrPhysical) throws SQLException {
        String query = "INSERT INTO books (title, author, fk_books_idreader, read_or_not, digital_or_physical) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(query)) {
            insertStatement.setString(1, title);
            insertStatement.setString(2, author);
            insertStatement.setInt(3, userId);
            insertStatement.setString(4, readOrNot);
            insertStatement.setString(5, digitalOrPhysical);

            insertStatement.executeUpdate();
            System.out.println(title + " ------- Added successfully");
        }
    }

    public boolean isBook(String bookTitle) throws SQLException {
        String query = "SELECT * FROM books WHERE title = ?";
        boolean bookfound = true;
        int attemptCount = 0;
        try (PreparedStatement checkStatement = connection.prepareStatement(query)) {
            checkStatement.setString(1, bookTitle);
            try (ResultSet resultSet = checkStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public String getBookStatus(String bookTitle) throws SQLException {
        String query = "SELECT read_or_not FROM books WHERE title = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(query)) {
            selectStatement.setString(1, bookTitle);
            try(ResultSet resultSet = selectStatement.executeQuery()){
                if (resultSet.next()){
                    return resultSet.getString("read_or_not");
                } else{
                    throw new SQLException("book not found");
                }
            }
        }
    }

    public int updateBookStatus(String newStatus, String bookTitle) throws SQLException {
        String query = "UPDATE books SET read_or_not = ? WHERE title = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(query)) {
            updateStatement.setString(1, newStatus);
            updateStatement.setString(2, bookTitle);

            int rowsAffected = updateStatement.executeUpdate();
            return rowsAffected;
        }
    }
}

