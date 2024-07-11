package org.example;
import java.sql.*;


public class BookHandler {
    private Connection connection;
    /**
     * Constructor to initialize the BookHandler with a database connection.
     *
     * @param connection The database connection
     */
    public BookHandler(Connection connection) {
        this.connection = connection;
    }

    /**
     * Adds a book to the database.
     *
     * @param title The title of the book
     * @param author The author of the book
     * @param userId The ID of the user adding the book
     * @param readOrNot Indicates if the book has been read (Y or N)
     * @param digitalOrPhysical Indicates if the book is digital or physical (D or P)
     * @throws SQLException if a database access error occurs
     */
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

    /**
     * Checks if a book exists in the database.
     *
     * @param bookTitle The title of the book to check
     * @return true if the book exists, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean isBook(String bookTitle) throws SQLException {
        String query = "SELECT * FROM books WHERE title = ?";
//        boolean bookfound = true;
//        int attemptCount = 0;
        try (PreparedStatement checkStatement = connection.prepareStatement(query)) {
            checkStatement.setString(1, bookTitle);
            try (ResultSet resultSet = checkStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    /**
     * Gets the read status of a book.
     *
     * @param bookTitle The title of the book
     * @return The read status of the book ("Y" for read, "N" for not read)
     * @throws SQLException if a database access error occurs or if the book is not found
     */
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

    /**
     * Updates the read status of a book.
     *
     * @param newStatus The new read status of the book ("Y" for read, "N" for not read)
     * @param bookTitle The title of the book
     * @return The number of rows affected by the update
     * @throws SQLException if a database access error occurs
     */
    public int updateBookStatus(String newStatus, String bookTitle) throws SQLException {
        String query = "UPDATE books SET read_or_not = ? WHERE title = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(query)) {
            updateStatement.setString(1, newStatus);
            updateStatement.setString(2, bookTitle);

            int rowsAffected = updateStatement.executeUpdate();
            return rowsAffected;
        }
    }

    /**
     * Retrieves books for a user based on their read status.
     *
     * @param userId The ID of the user
     * @param choice The read status to filter books ("Y" for read, "N" for not read)
     * @throws SQLException if a database access error occurs
     */
    public void getBooks(int userId, String choice) throws SQLException{
        String selectQuery = "SELECT title, author, digital_or_physical FROM books WHERE fk_books_idreader = ? and read_or_not = ? ORDER BY title ASC";
        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)){
            selectStatement.setInt(1, userId);
            selectStatement.setString(2, choice);

            try (ResultSet resultSet = selectStatement.executeQuery()){
                System.out.printf("%-40s %-29s %-10s%n", "-----TITLE-----", "-----AUTHOR-----", "-----FORMAT-----");
                while (resultSet.next()) {
                    String title = resultSet.getString("title");
                    String author = resultSet.getString("author");
                    String format = resultSet.getString("digital_or_physical");

                    if (format.equalsIgnoreCase("D")) {
                        format = "Digital";
                    } else {
                        format = "Physical";
                    }
                    System.out.printf("%-40s %-34s %-10s%n", title, author, format);
                }
                System.out.println();
            }
        }
    }
}

