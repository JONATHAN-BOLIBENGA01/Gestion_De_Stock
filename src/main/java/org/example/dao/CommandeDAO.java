package org.example.dao;


import org.example.dbManger.dbConnection;
import org.example.product.Commande;
import org.example.product.Produit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeDAO {
    public static void saveCommande(Commande commande) {
        String query = "INSERT INTO commandes (date_commande, est_livree, admin_email) VALUES (?, ?, ?)";

        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setTimestamp(1, new Timestamp(commande.getDateCommande().getTime()));
            stmt.setBoolean(2, commande.isEstLivree());
            stmt.setString(3, commande.getAdminEmail());
            stmt.executeUpdate();

            // Récupération de l'ID généré
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    commande.setId(generatedKeys.getLong(1));

                    // Sauvegarde des lignes de commande
                    saveLignesCommande(commande);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la sauvegarde de la commande : " + e.getMessage());
        }
    }

    private static void saveLignesCommande(Commande commande) throws SQLException {
        String query = "INSERT INTO lignes_commande (commande_id, produit_id, quantite) VALUES (?, ?, ?)";

        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (Commande.LigneCommande ligne : commande.getLignesCommande()) {
                stmt.setLong(1, commande.getId());
                stmt.setLong(2, ligne.getProduit().getId());
                stmt.setInt(3, ligne.getQuantite());
                stmt.addBatch();
            }

            stmt.executeBatch();
        }
    }

    public static List<Commande> getCommandesByAdmin(String adminEmail) {
        List<Commande> commandes = new ArrayList<>();
        String query = "SELECT * FROM commandes WHERE admin_email = ?";

        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, adminEmail);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Commande commande = new Commande(rs.getString("admin_email"));
                commande.setId(rs.getLong("id"));
                commande.setDateCommande(new Date(rs.getTimestamp("date_commande").getTime()));
                commande.setEstLivree(rs.getBoolean("est_livree"));
                commande.getLignesCommande().addAll(getLignesCommande(commande.getId(), conn));
                commandes.add(commande);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des commandes : " + e.getMessage());
        }

        return commandes;
    }

    public static List<Commande.LigneCommande> getLignesCommande(Long commandeId, Connection conn) throws SQLException {
        List<Commande.LigneCommande> lignes = new ArrayList<>();
        String query = "SELECT lc.*, p.* FROM lignes_commande lc JOIN produits p ON lc.produit_id = p.id WHERE lc.commande_id = ?";

        try (
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, commandeId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Produit produit = ProduitDAO.getProduitById(rs.getLong("produit_id"));
                int quantite = rs.getInt("quantite");
                lignes.add(new Commande.LigneCommande(produit, quantite));
            }
        }

        return lignes;
    }

    public static List<Commande> getCommandesByVendeur(String vendeurEmail) {
        List<Commande> commandes = new ArrayList<>();
        String query = "SELECT DISTINCT c.* FROM commandes c " +
                "JOIN lignes_commande lc ON c.id = lc.commande_id " +
                "JOIN produits p ON lc.produit_id = p.id " +
                "WHERE p.vendeur_email = ?";

        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, vendeurEmail);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Commande commande = new Commande(rs.getString("admin_email"));
                commande.setId(rs.getLong("id"));
                commande.setDateCommande(new Date(rs.getTimestamp("date_commande").getTime()));
                commande.setEstLivree(rs.getBoolean("est_livree"));

                // On charge uniquement les lignes de commande pour ce vendeur
                commande.getLignesCommande().addAll(getLignesCommandeForVendeur(commande.getId(), vendeurEmail));
                commandes.add(commande);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des commandes : " + e.getMessage());
        }

        return commandes;
    }

    private static List<Commande.LigneCommande> getLignesCommandeForVendeur(Long commandeId, String vendeurEmail) throws SQLException {
        List<Commande.LigneCommande> lignes = new ArrayList<>();
        String query = "SELECT lc.*, p.* FROM lignes_commande lc " +
                "JOIN produits p ON lc.produit_id = p.id " +
                "WHERE lc.commande_id = ? AND p.vendeur_email = ?";

        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, commandeId);
            stmt.setString(2, vendeurEmail);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Produit produit = ProduitDAO.getProduitById(rs.getLong("produit_id"));
                int quantite = rs.getInt("quantite");
                lignes.add(new Commande.LigneCommande(produit, quantite));
            }
        }
        return lignes;
    }
    public static String getClientEmailForCommande(Long commandeId) {
        String query = "SELECT admin_email FROM commandes WHERE id = ?";
        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, commandeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("admin_email");
            }
        } catch (SQLException e) {
            System.out.println("Erreur récupération email client: " + e.getMessage());
        }
        return null;
    }
}
