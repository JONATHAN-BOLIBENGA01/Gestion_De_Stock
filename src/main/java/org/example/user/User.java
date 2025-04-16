package org.example.user;

import org.example.dbManger.dbConnection;

import java.sql.*;
import java.util.List;

public abstract class User {
    protected String name;
    protected String email;
    protected String password;

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public static boolean emailExiste(String email) {
        String query = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
            return false;
        }
    }

    public static User seConnecter(String email, String password) {
        String query = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                String role = rs.getString("role");
                switch (role) {
                    case "admin":
                        return new Admin(name, email, password);
                    case "vendeur":
                        String matricule = rs.getString("matricule");
                        return new Vendeur(name, email, password, matricule);
                    case "client":
                        return new Client(name, email, password);
                    default:
                        return null;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
        }
        return null;
    }

    public abstract void sInscrire();

    public void enregistrerDansBaseDeDonnees() {
        String query = "INSERT INTO users (name, email, password, role, matricule) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, this.name);
            stmt.setString(2, this.email);
            stmt.setString(3, this.password);

            String role = this.getClass().getSimpleName().toLowerCase();
            stmt.setString(4, role);

            if (this instanceof Vendeur) {
                stmt.setString(5, ((Vendeur) this).getMatricule());
            } else {
                stmt.setNull(5, Types.VARCHAR);
            }

            stmt.executeUpdate();
            System.out.println("Utilisateur enregistré avec succès.");
        } catch (SQLException e) {
            System.out.println("Erreur d'enregistrement : " + e.getMessage());
        }
    }
}