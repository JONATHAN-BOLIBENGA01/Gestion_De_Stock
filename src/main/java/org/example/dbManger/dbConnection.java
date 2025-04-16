package org.example.dbManger;

import java.sql.*;

public class dbConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/gstock";
    private static final String USER = "jbw";
    private static final String PASSWORD = "admin";

    public static Connection connect() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connexion réussie !");
            return conn;
        } catch (SQLException e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
            return null;
        }
    }

    public static void close(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Erreur de fermeture de la connexion : " + e.getMessage());
        }
    }
}
