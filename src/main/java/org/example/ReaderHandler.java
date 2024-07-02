package org.example;

import javax.xml.transform.Result;
import java.io.Reader;
import java.sql.*;

/**
* Handles reader-related duties in Book Tracker.
*/
public class ReaderHandler {
    private Connection connection;

    /**
    * Constructor initializes ReaderHandler with db connection
    *
    * @param connection the database connection
    */
    public ReaderHandler(Connection connection) {
    this.connection = connection;
    }

    /**
    * Retrieves ID of reader from database based on username.
    *
    * @param userName The name of reader
    * @return The ID of reader, or -1 if reader not found
    */
    public int getReaderId(String userName) {
        String query = "SELECT idreader FROM readers WHERE readername = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(query)) {
            selectStatement.setString(1, userName);
            ResultSet resultset = selectStatement.executeQuery();
            if (resultset.next()) {
                return resultset.getInt("idreader");
            } else {
                return -1;
            }
        } catch (SQLException e)
            {e.printStackTrace();
            return -1;
        }
    }

    /**
    * Adds new reader to database.
    *
    * @param userName The name of new reader
    * @throws SQLException if database access error occurs
    */
    public void addReader(String userName) throws SQLException {
        String query = "INSERT INTO readers (readername) VALUES (?)";
        ResultSet resultSet;
        try (PreparedStatement insertStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
            insertStatement.setString(1, userName);
            insertStatement.executeUpdate();
            resultSet = insertStatement.getGeneratedKeys();
        }
    }
}
