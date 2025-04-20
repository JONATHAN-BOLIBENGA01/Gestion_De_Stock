package org.example.dao;

import org.example.dbManger.dbConnection;
import org.example.product.Categorie;
import org.example.product.Produit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAO {
    public static void saveProduit(Produit produit) {
        saveProduit(produit, null);
    }

    public static void saveProduit(Produit produit, String vendeurEmail) {
        java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());
        String query;
        boolean isInsert = produit.getId() == null;

        if (isInsert) {
            query = "INSERT INTO produits (nom, prix, quantite_stock, seuil_alerte, date_ajout, categorie_id" +
                    (vendeurEmail != null ? ", vendeur_email" : "") + ") VALUES (?, ?, ?, ?, ?, ?" +
                    (vendeurEmail != null ? ", ?" : "") + ")";
        } else {
            query = "UPDATE produits SET nom = ?, prix = ?, quantite_stock = ?, seuil_alerte = ?, categorie_id = ? WHERE id = ?";
        }

        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            int paramIndex = 1;
            stmt.setString(paramIndex++, produit.getNom());
            stmt.setFloat(paramIndex++, produit.getPrix());
            stmt.setInt(paramIndex++, produit.getQuantiteStock());
            stmt.setInt(paramIndex++, produit.getSeuilAlerte());

            if (isInsert) {
                stmt.setDate(paramIndex++, sqlDate);
                stmt.setLong(paramIndex++, produit.getCategorie().getId());
                if (vendeurEmail != null) {
                    stmt.setString(paramIndex++, vendeurEmail);
                }
            } else {
                stmt.setLong(paramIndex++, produit.getCategorie().getId());
                if (vendeurEmail != null) {
                    stmt.setString(paramIndex++, vendeurEmail);
                }
                stmt.setLong(paramIndex++, produit.getId());
            }

            stmt.executeUpdate();

            if (isInsert) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        produit.setId(generatedKeys.getLong(1));
                    }
                }
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
                // Récupération de la catégorie associée
                Long categorieId = rs.getLong("categorie_id");
                Categorie categorie = CategorieDAO.getCategorieById(Math.toIntExact(categorieId));

                // Création du produit avec la catégorie
                Produit produit = new Produit(
                        rs.getLong("id"),
                        rs.getString("nom"),
                        rs.getFloat("prix"),
                        rs.getInt("quantite_stock"),
                        rs.getInt("seuil_alerte"),
                        categorie
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
                    // Récupérer l'ID de la catégorie
                    Long categorieId = rs.getLong("categorie_id");

                    // Récupérer la catégorie à partir de l'ID
                    Categorie categorie = CategorieDAO.getCategorieById(Math.toIntExact(categorieId));

                    // Créer le produit avec la catégorie
                    Produit produit = new Produit(
                            rs.getLong("id"),
                            rs.getString("nom"),
                            rs.getFloat("prix"),
                            rs.getInt("quantite_stock"),
                            rs.getInt("seuil_alerte"),
                            categorie // Ajouter la catégorie au produit
                    );
                    produit.setDateAjout(rs.getDate("date_ajout"));
                    return produit;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du produit par ID : " + e.getMessage());
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

    public static String getVendeurEmailForProduit(Long id) {
        String query = "SELECT vendeur_email FROM produits WHERE id = ?";
        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("vendeur_email");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de l'email du vendeur : " + e.getMessage());
        }

        return null;
    }
}
