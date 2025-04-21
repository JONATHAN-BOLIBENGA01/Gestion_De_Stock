package org.example.dao;

import org.example.dbManger.dbConnection;
import org.example.product.Categorie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieDAO {
    public static boolean saveCategorie(Categorie cat) {
        String query = "INSERT INTO categories (nom) VALUES (?)";
        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, cat.getNom());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur ajout catégorie : " + e.getMessage());
        }
        return false;
    }

    public static List<Categorie> getAllCategories() {
        List<Categorie> list = new ArrayList<>();
        String query = "SELECT * FROM categories";

        try (Connection conn = dbConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Categorie cat = new Categorie();
                cat.setId(rs.getLong("id"));
                cat.setNom(rs.getString("nom"));
                list.add(cat);
            }

        } catch (SQLException e) {
            System.out.println("Erreur récupération des catégories : " + e.getMessage());
        }

        return list;
    }

    public static Categorie getCategorieById(int id) {
        String query = "SELECT * FROM categories WHERE id = ?";
        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Categorie cat = new Categorie();
                    cat.setId(rs.getLong("id"));
                    cat.setNom(rs.getString("nom"));
                    return cat;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur récupération catégorie : " + e.getMessage());
        }
        return null;
    }
}

