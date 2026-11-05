/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.eccomerceplatform;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
private static final String SERVER_URL = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String URL = "jdbc:mysql://localhost:3306/ecommerce_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC driver not found. Add mysql-connector-j to the project libraries.");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static String initializeDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
                 Statement st = conn.createStatement()) {
                st.executeUpdate("CREATE DATABASE IF NOT EXISTS ecommerce_db");
            }

            try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
                st.executeUpdate("CREATE TABLE IF NOT EXISTS users ("
                        + "user_id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "username VARCHAR(30) NOT NULL UNIQUE, "
                        + "password VARCHAR(100) NOT NULL, "
                        + "name VARCHAR(100) NOT NULL, "
                        + "email VARCHAR(100) NOT NULL, "
                        + "role VARCHAR(20) NOT NULL)");

                st.executeUpdate("CREATE TABLE IF NOT EXISTS products ("
                        + "product_id INT PRIMARY KEY, "
                        + "name VARCHAR(100) NOT NULL, "
                        + "price DOUBLE NOT NULL, "
                        + "category VARCHAR(50) NOT NULL, "
                        + "stock INT NOT NULL)");

                st.executeUpdate("CREATE TABLE IF NOT EXISTS product_ratings ("
                        + "user_id INT NOT NULL, "
                        + "product_id INT NOT NULL, "
                        + "rating INT NOT NULL, "
                        + "PRIMARY KEY (user_id, product_id))");

                st.executeUpdate("CREATE TABLE IF NOT EXISTS orders ("
                        + "order_id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "user_id INT NOT NULL, "
                        + "total_price DOUBLE NOT NULL, "
                        + "payment_method VARCHAR(30) NOT NULL, "
                        + "order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

                st.executeUpdate("CREATE TABLE IF NOT EXISTS order_items ("
                        + "item_id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "order_id INT NOT NULL, "
                        + "product_id INT NOT NULL, "
                        + "product_name VARCHAR(100) NOT NULL, "
                        + "unit_price DOUBLE NOT NULL, "
                        + "quantity INT NOT NULL)");

                st.executeUpdate("INSERT IGNORE INTO users (username, password, name, email, role) "
                        + "VALUES ('admin', 'admin123', 'Administrator', 'admin@shop.com', 'Admin')");
            }
            return null;
        } catch (ClassNotFoundException e) {
            return "MySQL JDBC driver not found. Add mysql-connector-j to the project libraries.";
        } catch (SQLException e) {
            return "Database setup failed: " + e.getMessage();
        }
    }
}
