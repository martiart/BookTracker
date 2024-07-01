package org.example;

import java.sql.Connection;

public class BookHandler {
    private Connection connection;

    public BookHandler(Connection connection){
        this.connection = connection;
    }
}
