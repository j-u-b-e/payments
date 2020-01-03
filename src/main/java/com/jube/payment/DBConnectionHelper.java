package com.jube.payment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionHelper {
    private static final String DEFAULT_URL = "jdbc:h2:./src/main/resources/h2/revolut";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static Connection getNewConnection() throws SQLException {
        return DriverManager.getConnection(DEFAULT_URL, USER, PASSWORD);
    }
}
