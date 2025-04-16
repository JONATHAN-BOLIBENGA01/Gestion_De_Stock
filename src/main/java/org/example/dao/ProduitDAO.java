package org.example.dao;

import org.example.dbManger.dbConnection;
import org.example.product.Produit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAO {

    public static void saveProduit(Produit produit) {
        String query;
        if (produit.getId() == null) {
            query = "INSERT INTO produits (nom, prix, quantite_stock, seuil_alerte, date_ajout) VALUES (?, ?, ?, ?, ?)";
        } else {
            query = "UPDATE produits SET nom = ?, prix = ?, quantite_stock = ?, seuil_alerte = ? WHERE id = ?";
        }

        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, produit.getNom());
            stmt.setFloat(2, produit.getPrix());
            stmt.setInt(3, produit.getQuantiteStock());
            stmt.setInt(4, produit.getSeuilAlerte());

            if (produit.getId() == null) {
                stmt.setDate(5, new java.sql.Date(produit.getDateAjout().getTime()));
                stmt.executeUpdate();

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        produit.setId(generatedKeys.getLong(1));
                    }
                }
            } else {
                stmt.setLong(5, produit.getId());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la sauvegarde du produit : " + e.getMessage());
        }
    }

    public static List<Produit> getAllProduits() {
        List<Produit> produits = new ArrayList<>();
        String query = "SELECT * FROM produits";

        try (Connection conn = dbConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Produit produit = new Produit(
                        rs.getLong("id"),
                        rs.getString("nom"),
                        rs.getFloat("prix"),
                        rs.getInt("quantite_stock"),
                        rs.getInt("seuil_alerte")
                );
                produit.setDateAjout(rs.getDate("date_ajout"));
                produits.add(produit);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des produits : " + e.getMessage());
        }
        return produits;
    }

    public static Produit getProduitById(Long id) {
        String query = "SELECT * FROM produits WHERE id = ?";

        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Produit produit = new Produit(
                            rs.getLong("id"),
                            rs.getString("nom"),
                            rs.getFloat("prix"),
                            rs.getInt("quantite_stock"),
                            rs.getInt("seuil_alerte")
                    );
                    produit.setDateAjout(rs.getDate("date_ajout"));
                    return produit;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du produit : " + e.getMessage());
        }
        return null;
    }

    public static boolean deleteProduit(Long id) {
        String query = "DELETE FROM produits WHERE id = ?";

        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du produit : " + e.getMessage());
            return false;
        }
    }
}