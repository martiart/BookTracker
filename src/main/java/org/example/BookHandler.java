package org.example;
import java.sql.*;

public class BookHandler {
    private Connection connection;

    public BookHandler(Connection connection){
        this.connection = connection;
    }

    public void addBook(String title, String author, int userId, String readOrNot, String digitalOrPhysical) throws SQLException{
        String query = "INSERT INTO books (title, author, fk_books_idreader, read_or_not, digital_or_physical) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(query)){
            insertStatement.setString(1, title);
            insertStatement.setString(2, author);
            insertStatement.setInt(3, userId);
            insertStatement.setString(4, readOrNot);
            insertStatement.setString(5, digitalOrPhysical);

            insertStatement.executeUpdate();

        }
    }
}
