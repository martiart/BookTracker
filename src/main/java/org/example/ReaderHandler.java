package org.example;

import javax.xml.transform.Result;
import java.io.Reader;
import java.sql.*;


public class ReaderHandler {
    private Connection connection;

    public ReaderHandler(Connection connection) {
        this.connection = connection;
    }

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
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

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
