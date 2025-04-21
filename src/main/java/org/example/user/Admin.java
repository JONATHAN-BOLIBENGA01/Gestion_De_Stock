package org.example.user;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.example.dao.*;
import org.example.dbManger.dbConnection;
import org.example.product.*;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Admin extends User {
    private static final String LOGO_PATH = "src/main/resources/logo.png";
    private static final String REPORTS_DIR = "rapports";
    private static final AtomicInteger invoiceCounter = new AtomicInteger(1000);

    public Admin(String name, String email, String password) {
        super(name, email, password);
    }

    @Override
    public void sInscrire() {
        System.out.println("admin deja inscrit");
    }

    // Gestion des produits
    public void ajouterProduit(Produit produit, String vendeurEmail) {
        ProduitDAO.saveProduit(produit, vendeurEmail);
    }

    public void mettreAJourProduit(Long id, String nom, float prix, int quantiteStock, int seuilAlerte) {
        Produit produit = ProduitDAO.getProduitById(id);
        if (produit != null) {
            produit.setNom(nom);
            produit.setPrix(prix);
            produit.setQuantiteStock(quantiteStock);
            produit.setSeuilAlerte(seuilAlerte);
            ProduitDAO.saveProduit(produit, ProduitDAO.getVendeurEmailForProduit(id));
        }
    }

    public boolean supprimerProduit(Long id) {
        return ProduitDAO.deleteProduit(id);
    }

    // Gestion des commandes
    public Commande creerCommandePourProduitsSousSeuil() {
        List<Produit> produits = ProduitDAO.getAllProduits();
        Commande commande = new Commande(this.getEmail());

        for (Produit produit : produits) {
            if (produit.besoinReapprovisionnement()) {
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
        try (Connection conn = dbConnection.connect()) {
            conn.setAutoCommit(false);

            Commande commande = getCommandeById(commandeId, conn);
            if (commande == null) {
                System.out.println("Commande non trouvée");
                return;
            }

            for (Commande.LigneCommande ligne : commande.getLignesCommande()) {
                Produit produit = ligne.getProduit();
                produit.setQuantiteStock(produit.getQuantiteStock() + ligne.getQuantite());
                ProduitDAO.saveProduit(produit, String.valueOf(conn));
            }

            String updateQuery = "UPDATE commandes SET est_livree = TRUE WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                stmt.setLong(1, commandeId);
                stmt.executeUpdate();
            }

            conn.commit();
            System.out.println("✅ Livraison validée avec succès !");
        } catch (SQLException e) {
            System.out.println("❌ Erreur validation livraison: " + e.getMessage());
        }
    }

    // Génération de rapports
    public void genererRapportCompletPDF() {
        String fileName = REPORTS_DIR + "/rapport_" + new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date()) + ".pdf";

        try {
            Files.createDirectories(Paths.get(REPORTS_DIR));

            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            addLogo(document);
            addTitle(document, "RAPPORT COMPLET");

            addProduitsSection(document);
            addVentesSection(document);
            addClientsSection(document);
            addReapproSection(document);

            addSignatureArea(document);

            document.close();
            System.out.println("✅ Rapport généré: " + fileName);
        } catch (Exception e) {
            System.out.println("❌ Erreur génération rapport: " + e.getMessage());
        }
    }


    private void addLogo(Document document) throws Exception {
        try {
            Image logo = Image.getInstance(LOGO_PATH);
            logo.scaleToFit(120, 120);
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);
        } catch (Exception e) {
            System.out.println("⚠️ Logo non trouvé");
        }
    }

    private void addTitle(Document document, String title) throws DocumentException {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.DARK_GRAY);
        Paragraph p = new Paragraph(title, font);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingAfter(20);
        document.add(p);

        Paragraph date = new Paragraph("Généré le: " + new SimpleDateFormat("dd/MM/yyyy 'à' HH:mm").format(new Date()));
        date.setAlignment(Element.ALIGN_CENTER);
        document.add(date);
    }

    private void addProduitsSection(Document document) throws Exception {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLUE);
        document.add(new Paragraph("\n1. PRODUITS EN STOCK", font));

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);

        addTableHeader(table, "ID");
        addTableHeader(table, "Nom");
        addTableHeader(table, "Prix");
        addTableHeader(table, "Stock");
        addTableHeader(table, "Seuil");
        addTableHeader(table, "Vendeur");

        List<Produit> produits = ProduitDAO.getAllProduits();
        for (Produit p : produits) {
            table.addCell(p.getId().toString());
            table.addCell(p.getNom());
            table.addCell(String.format("%.2f€", p.getPrix()));

            PdfPCell stockCell = new PdfPCell(new Phrase(String.valueOf(p.getQuantiteStock())));
            if (p.besoinReapprovisionnement()) {
                stockCell.setBackgroundColor(BaseColor.ORANGE);
            }
            table.addCell(stockCell);

            table.addCell(String.valueOf(p.getSeuilAlerte()));
            table.addCell(ProduitDAO.getVendeurEmailForProduit(p.getId()));
        }

        document.add(table);
    }

    private void addVentesSection(Document document) throws Exception {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLUE);
        document.add(new Paragraph("\n2. VENTES", font));

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);

        addTableHeader(table, "Produit");
        addTableHeader(table, "Quantité");
        addTableHeader(table, "Prix U.");
        addTableHeader(table, "Total");

        List<Commande> commandes = CommandeDAO.getCommandesByAdmin(this.getEmail());
        Map<Long, Integer> ventes = new HashMap<>();

        for (Commande cmd : commandes) {
            for (Commande.LigneCommande l : cmd.getLignesCommande()) {
                ventes.merge(l.getProduit().getId(), l.getQuantite(), Integer::sum);
            }
        }

        double caTotal = 0;
        for (Map.Entry<Long, Integer> entry : ventes.entrySet()) {
            Produit p = ProduitDAO.getProduitById(entry.getKey());
            double total = p.getPrix() * entry.getValue();
            caTotal += total;

            table.addCell(p.getNom());
            table.addCell(String.valueOf(entry.getValue()));
            table.addCell(String.format("%.2f€", p.getPrix()));
            table.addCell(String.format("%.2f€", total));
        }

        document.add(table);
        document.add(new Paragraph(String.format("\nChiffre d'affaires total: %.2f€", caTotal),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
    }

    private void addClientsSection(Document document) throws Exception {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLUE);
        document.add(new Paragraph("\n3. CLIENTS", font));

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);

        addTableHeader(table, "Email");
        addTableHeader(table, "Commandes");
        addTableHeader(table, "Dépenses");

        Map<String, ClientStats> clients = new HashMap<>();
        List<Commande> commandes = CommandeDAO.getCommandesByAdmin(this.getEmail());

        for (Commande cmd : commandes) {
            ClientStats stats = clients.computeIfAbsent(cmd.getAdminEmail(), k -> new ClientStats());
            stats.commandes++;
            stats.montantTotal += cmd.calculerTotal();
        }

        for (Map.Entry<String, ClientStats> entry : clients.entrySet()) {
            table.addCell(entry.getKey());
            table.addCell(String.valueOf(entry.getValue().commandes));
            table.addCell(String.format("%.2f€", entry.getValue().montantTotal));
        }

        document.add(table);
    }

    private void addReapproSection(Document document) throws Exception {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLUE);
        document.add(new Paragraph("\n4. RÉAPPROVISIONNEMENTS", font));

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);

        addTableHeader(table, "N°");
        addTableHeader(table, "Date");
        addTableHeader(table, "Produit");
        addTableHeader(table, "Quantité");
        addTableHeader(table, "Statut");

        List<Commande> commandes = CommandeDAO.getCommandesByAdmin(this.getEmail());
        for (Commande cmd : commandes) {
            for (Commande.LigneCommande l : cmd.getLignesCommande()) {
                if (l.getProduit().besoinReapprovisionnement()) {
                    table.addCell(cmd.getId().toString());
                    table.addCell(new SimpleDateFormat("dd/MM/yyyy").format(cmd.getDateCommande()));
                    table.addCell(l.getProduit().getNom());
                    table.addCell(String.valueOf(l.getQuantite()));
                    table.addCell(cmd.isEstLivree() ? "Livré" : "En attente");
                }
            }
        }

        document.add(table);
    }

    private void addSignatureArea(Document document) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(30);
        table.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.setSpacingBefore(40);

        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.BOTTOM);
        cell.setFixedHeight(40);
        table.addCell(cell);

        PdfPCell textCell = new PdfPCell(new Phrase("Signature",
                FontFactory.getFont(FontFactory.HELVETICA, 10)));
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(textCell);

        document.add(table);
    }

    private void addTableHeader(PdfPTable table, String header) {
        PdfPCell cell = new PdfPCell(new Phrase(header,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
        cell.setBackgroundColor(new BaseColor(70, 130, 180));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private Commande getCommandeById(Long commandeId, Connection conn) throws SQLException {
        String query = "SELECT * FROM commandes WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, commandeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Commande commande = new Commande(rs.getString("admin_email"));
                commande.setId(rs.getLong("id"));
                commande.setDateCommande(new Date(rs.getTimestamp("date_commande").getTime()));
                commande.setEstLivree(rs.getBoolean("est_livree"));
                commande.getLignesCommande().addAll(CommandeDAO.getLignesCommande(commandeId, conn));
                return commande;
            }
        }
        return null;
    }

    private static class ClientStats {
        int commandes = 0;
        double montantTotal = 0;
    }
}