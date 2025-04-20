package org.example.user;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.example.dao.CommandeDAO;
import org.example.dao.ProduitDAO;
import org.example.dbManger.dbConnection;
import org.example.product.Commande;
import org.example.product.GestionStock;
import org.example.product.Produit;

import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class Admin extends User {
    public Admin(String name, String email, String password) {
        super(name, email, password);
    }

    @Override
    public void sInscrire() {
        // L'admin ne s'inscrit pas normalement
    }

    public void inscrireVendeur(String name, String email, String password, String matricule) {
        if (emailExiste(email)) {
            System.out.println("Erreur : Cet email est déjà utilisé.");
            return;
        }
        Vendeur vendeur = new Vendeur(name, email, password, matricule);
        vendeur.enregistrerDansBaseDeDonnees();
        System.out.println("Vendeur inscrit avec succès.");
    }

    public void ajouterProduit(GestionStock gestionStock, Produit produit) {
        gestionStock.ajouterProduit(produit);
    }

    public void supprimerProduit(GestionStock gestionStock, Long id) {
        gestionStock.supprimerProduit(id);
    }

    public void genererRapport() {
        System.out.println("Génération du rapport...");
    }

    public Commande creerCommandePourProduitsSousSeuil() {
        List<Produit> produits = ProduitDAO.getAllProduits();
        Commande commande = new Commande(this.getEmail());

        for (Produit produit : produits) {
            if (produit.besoinReapprovisionnement()) {
                // Quantité à commander = seuil d'alerte * 2 (par exemple)
                int quantiteACommander = produit.getSeuilAlerte() * 2;
                commande.ajouterProduit(produit, quantiteACommander);
            }
        }

        if (!commande.getLignesCommande().isEmpty()) {
            CommandeDAO.saveCommande(commande);
            return commande;
        }

        return null;
    }

    public void validerLivraisonCommande(Long commandeId) {

        Commande commande = getCommandeById(commandeId);

        if (commande == null) {
            System.out.println("Commande non trouvée.");
            return;
        }

        if (commande.isEstLivree()) {
            System.out.println("Cette commande a déjà été livrée.");
            return;
        }

        for (Commande.LigneCommande ligne : commande.getLignesCommande()) {
            Produit produit = ligne.getProduit();
            int nouvelleQuantite = produit.getQuantiteStock() + ligne.getQuantite();
            produit.setQuantiteStock(nouvelleQuantite);
            ProduitDAO.saveProduit(produit);
        }

        // 3. Marquer la commande comme livrée
        String updateQuery = "UPDATE commandes SET est_livree = TRUE WHERE id = ?";

        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setLong(1, commandeId);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Livraison validée avec succès !");
                commande.setEstLivree(true);
            } else {
                System.out.println("Échec de la validation de la livraison.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la validation de la livraison : " + e.getMessage());
        }
    }

    private Commande getCommandeById(Long commandeId) {
        String query = "SELECT * FROM commandes WHERE id = ?";

        try (Connection conn = dbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, commandeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Commande commande = new Commande(rs.getString("admin_email"));
                commande.setId(rs.getLong("id"));
                commande.setDateCommande(new Date(rs.getTimestamp("date_commande").getTime()));
                commande.setEstLivree(rs.getBoolean("est_livree"));

                commande.getLignesCommande().addAll(CommandeDAO.getLignesCommande(commandeId));
                return commande;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de la commande : " + e.getMessage());
        }

        return null;
    }

    public List<Commande> getHistoriqueCommandes() {
        List<Commande> commandes = CommandeDAO.getCommandesByAdmin(this.getEmail());
        commandes.sort((c1, c2) -> c2.getDateCommande().compareTo(c1.getDateCommande()));
        return commandes;
    }

    public void genererRapportPDF() {
        List<Produit> produits = ProduitDAO.getAllProduits();
        String fileName = "rapport_stock_" + new SimpleDateFormat("yyyyMMdd").format(new java.util.Date()) + ".pdf";

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            // Titre
            Font fontTitre = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE);
            Paragraph titre = new Paragraph("Rapport de Stock", fontTitre);
            titre.setAlignment(Element.ALIGN_CENTER);
            titre.setSpacingAfter(20);
            document.add(titre);

            // Date
            Paragraph date = new Paragraph("Généré le: " + new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()));
            date.setSpacingAfter(20);
            document.add(date);

            // Tableau des produits
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);

            // En-têtes
            addTableHeader(table, "ID");
            addTableHeader(table, "Nom");
            addTableHeader(table, "Prix");
            addTableHeader(table, "Stock");
            addTableHeader(table, "Catégorie");

            // Données
            for (Produit p : produits) {
                table.addCell(p.getId().toString());
                table.addCell(p.getNom());
                table.addCell(String.format("%.2f €", p.getPrix()));
                table.addCell(String.valueOf(p.getQuantiteStock()));
                table.addCell(p.getCategorie() != null ? p.getCategorie().getNom() : "");
            }

            document.add(table);

            // Produits en alerte
            List<Produit> produitsAlerte = produits.stream()
                    .filter(Produit::besoinReapprovisionnement)
                    .toList();

            if (!produitsAlerte.isEmpty()) {
                Paragraph alerteTitre = new Paragraph("\nProduits nécessitant réapprovisionnement:",
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD));
                document.add(alerteTitre);

                PdfPTable tableAlerte = new PdfPTable(2);
                tableAlerte.setWidthPercentage(100);
                tableAlerte.setSpacingBefore(10);

                addTableHeader(tableAlerte, "Produit");
                addTableHeader(tableAlerte, "Stock actuel");

                for (Produit p : produitsAlerte) {
                    tableAlerte.addCell(p.getNom());
                    tableAlerte.addCell(p.getQuantiteStock() + " (seuil: " + p.getSeuilAlerte() + ")");
                }

                document.add(tableAlerte);
            }

            document.close();
            System.out.println("Rapport PDF généré avec succès: " + fileName);

        } catch (Exception e) {
            System.out.println("Erreur lors de la génération du PDF: " + e.getMessage());
        }
    }

    private void addTableHeader(PdfPTable table, String header) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setBorderWidth(1);
        cell.setPhrase(new Phrase(header));
        table.addCell(cell);
    }
}