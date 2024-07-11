package org.example;
//
//import javax.xml.transform.Result;
//import java.io.Reader;
//import java.sql.*;
//
///**
//* Handles reader-related duties in Book Tracker.
//*/
//public class ReaderHandler {
//    private Connection connection;
//
//    /**
//    * Constructor initializes ReaderHandler with db connection
//    *
//    * @param connection the database connection
//    */
//    public ReaderHandler(Connection connection) {
//        this.connection = connection;
//    }
//
//    /**
//    * Retrieves ID of reader from database based on username.
//    *
//    * @param userName The name of reader
//    * @return The ID of reader, or -1 if reader not found
//    */
//    public int getReaderId(String userName) {
//        String query = "SELECT idreader FROM readers WHERE readername = ?";
//        try (PreparedStatement selectStatement = connection.prepareStatement(query)) {
//            selectStatement.setString(1, userName);
//            ResultSet resultset = selectStatement.executeQuery();
//            if (resultset.next()) {
//                return resultset.getInt("idreader");
//            } else {
//                return -1;
//            }
//        } catch (SQLException e)
//            {e.printStackTrace();
//            return -1;
//        }
//    }
//
//    /**
//    * Adds new reader to database.
//    *
//    * @param userName The name of new reader
//    * @throws SQLException if database access error occurs
//    */
//    public void addReader(String userName) throws SQLException {
//        String query = "INSERT INTO readers (readername) VALUES (?)";
//        ResultSet resultSet;
//        try (PreparedStatement insertStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
//            insertStatement.setString(1, userName);
//            insertStatement.executeUpdate();
//            resultSet = insertStatement.getGeneratedKeys();
//        }
//    }
//}

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReaderHandler {
    private Connection connection;

    public ReaderHandler(Connection connection) {
        this.connection = connection;
    }

    public Reader addReader(String name) throws SQLException{
        String query = "INSERT INTO readers (readername) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
            System.out.println("Reader added successfully: " + name);

            return getReaderByName(name);
        }
    }

    public Reader getReaderByName(String name) throws SQLException {
        String query = "SELECT * FROM readers WHERE readername = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Reader(rs.getInt("idreader"), rs.getString("readername"));
            }
        }
        return null; // Reader not found
    }

//    public void updateReaderName(int readerId, String newName) {
//        String query = "UPDATE readers SET readername = ? WHERE idreader = ?";
//        try (PreparedStatement stmt = connection.prepareStatement(query)) {
//            stmt.setString(1, newName);
//            stmt.setInt(2, readerId);
//            int rowsUpdated = stmt.executeUpdate();
//            if (rowsUpdated > 0) {
//                System.out.println("Reader updated successfully. New name: " + newName);
//            } else {
//                System.out.println("Failed to update reader.");
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

//    public void deleteReader(int readerId) {
//        String query = "DELETE FROM readers WHERE idreader = ?";
//        try (PreparedStatement stmt = connection.prepareStatement(query)) {
//            stmt.setInt(1, readerId);
//            int rowsDeleted = stmt.executeUpdate();
//            if (rowsDeleted > 0) {
//                System.out.println("Reader deleted successfully.");
//            } else {
//                System.out.println("Failed to delete reader.");
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
}
