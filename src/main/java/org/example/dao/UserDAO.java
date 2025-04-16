package org.example.dao;

import java.sql.*;
import org.example.dbManger.dbConnection;
import org.example.user.Client;
import org.example.user.User;
import org.example.user.Vendeur;

public class UserDAO {

    public static void saveUser(User user) {
        String query = "INSERT INTO users (name, email, password, type) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getClass().getSimpleName());

            stmt.executeUpdate();
            System.out.println("Utilisateur ajouté avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'enregistrement de l'utilisateur : " + e.getMessage());
        }
    }

    public static User getUserByEmail(String email) {
        String query = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String password = rs.getString("password");
                String type = rs.getString("type");

                if ("Vendeur".equals(type)) {
                    return new Vendeur(name, email, password, rs.getString("matricule"));
                } else if ("Client".equals(type)) {
                    return new Client(name, email, password);
                }

            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de l'utilisateur : " + e.getMessage());
        }
        return null;
    }
}
