package com.demo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {

    private static final String URL = "jdbc:mysql://localhost:3306/taskDB";
    private static final String name = "root";
    private static final String password = "root";

        public static Connection getConnection() throws SQLException {

            return DriverManager.getConnection(URL, name, password);
    }
}
