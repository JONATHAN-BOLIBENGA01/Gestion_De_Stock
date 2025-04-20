package org.example.user;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.example.dao.*;
import org.example.product.*;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;

public class Admin extends User {
    private static final String LOGO_PATH = "src/main/resources/logo.png";
    private static final String REPORTS_DIR = "rapports";

    public Admin(String name, String email, String password) {
        super(name, email, password);
    }

    @Override
    public void sInscrire() {
        // L'admin ne s'inscrit pas normalement
    }

    public void genererRapportCompletPDF() {
        String fileName = REPORTS_DIR + "/rapport_complet_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf";

        try {
            // Créer le dossier rapports si inexistant
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get(REPORTS_DIR));

            Document document = new Document(PageSize.A4.rotate()); // Format paysage
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            // Ajouter le logo
            addLogo(document);

            // Titre principal
            addTitle(document, "RAPPORT COMPLET DE GESTION");

            // Section 1: Produits en stock
            addProductsSection(document);

            // Section 2: Produits vendus
            addSalesSection(document);

            // Section 3: Clients et achats
            addClientsSection(document);

            // Section 4: Commandes de réapprovisionnement
            addReordersSection(document);

            // Signature
            addSignatureArea(document);

            document.close();
            System.out.println("✅ Rapport complet généré: " + fileName);
        } catch (Exception e) {
            System.out.println("❌ Erreur génération rapport: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addLogo(Document document) throws Exception {
        try {
            Image logo = Image.getInstance(LOGO_PATH);
            logo.scaleToFit(150, 150);
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);
            document.add(Chunk.NEWLINE);
        } catch (Exception e) {
            System.out.println("⚠️ Logo non trouvé, continuation sans logo");
        }
    }

    private void addTitle(Document document, String title) throws DocumentException {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.DARK_GRAY);
        Paragraph p = new Paragraph(title, titleFont);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingAfter(30);
        document.add(p);

        // Date du rapport
        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.GRAY);
        Paragraph date = new Paragraph("Généré le: " + new SimpleDateFormat("dd/MM/yyyy à HH:mm").format(new Date()), dateFont);
        date.setAlignment(Element.ALIGN_CENTER);
        date.setSpacingAfter(20);
        document.add(date);
    }

    private void addProductsSection(Document document) throws Exception {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLUE);
        Paragraph sectionTitle = new Paragraph("1. PRODUITS EN STOCK", sectionFont);
        sectionTitle.setSpacingAfter(15);
        document.add(sectionTitle);

        List<Produit> produits = ProduitDAO.getAllProduits();

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        // En-têtes
        addTableHeader(table, "ID");
        addTableHeader(table, "Nom");
        addTableHeader(table, "Prix");
        addTableHeader(table, "Stock");
        addTableHeader(table, "Seuil");
        addTableHeader(table, "Catégorie");

        // Données
        for (Produit p : produits) {
            table.addCell(p.getId().toString());
            table.addCell(p.getNom());
            table.addCell(String.format("%.2f €", p.getPrix()));

            PdfPCell stockCell = new PdfPCell(new Phrase(String.valueOf(p.getQuantiteStock())));
            if (p.besoinReapprovisionnement()) {
                stockCell.setBackgroundColor(BaseColor.ORANGE);
            }
            table.addCell(stockCell);

            table.addCell(String.valueOf(p.getSeuilAlerte()));
            table.addCell(p.getCategorie() != null ? p.getCategorie().getNom() : "N/A");
        }

        document.add(table);

        // Statistiques
        long produitsEnAlerte = produits.stream().filter(Produit::besoinReapprovisionnement).count();
        document.add(new Paragraph("\nStatistiques:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
        document.add(new Paragraph(String.format("- Total produits: %d", produits.size())));
        document.add(new Paragraph(String.format("- Produits en alerte: %d", produitsEnAlerte)));
        document.add(Chunk.NEWLINE);
    }

    private void addSalesSection(Document document) throws Exception {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLUE);
        Paragraph sectionTitle = new Paragraph("2. PRODUITS VENDUS", sectionFont);
        sectionTitle.setSpacingAfter(15);
        document.add(sectionTitle);

        List<Commande> commandes = CommandeDAO.getAllCommandes();
        Map<Long, Integer> produitsVendus = new HashMap<>();

        // Calculer les quantités vendues
        for (Commande cmd : commandes) {
            for (Commande.LigneCommande ligne : cmd.getLignesCommande()) {
                produitsVendus.merge(ligne.getProduit().getId(), ligne.getQuantite(), Integer::sum);
            }
        }

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);

        addTableHeader(table, "Produit");
        addTableHeader(table, "Quantité vendue");
        addTableHeader(table, "CA généré");
        addTableHeader(table, "% du total");

        double caTotal = produitsVendus.entrySet().stream()
                .mapToDouble(e -> {
                    Produit p = ProduitDAO.getProduitById(e.getKey());
                    return p.getPrix() * e.getValue();
                })
                .sum();

        for (Map.Entry<Long, Integer> entry : produitsVendus.entrySet()) {
            Produit p = ProduitDAO.getProduitById(entry.getKey());
            double caProduit = p.getPrix() * entry.getValue();
            double percentage = (caProduit / caTotal) * 100;

            table.addCell(p.getNom());
            table.addCell(String.valueOf(entry.getValue()));
            table.addCell(String.format("%.2f €", caProduit));
            table.addCell(String.format("%.1f %%", percentage));
        }

        document.add(table);
        document.add(new Paragraph("\nChiffre d'affaires total: " + String.format("%.2f €", caTotal),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
        document.add(Chunk.NEWLINE);
    }

    private void addClientsSection(Document document) throws Exception {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLUE);
        Paragraph sectionTitle = new Paragraph("3. CLIENTS ET ACHATS", sectionFont);
        sectionTitle.setSpacingAfter(15);
        document.add(sectionTitle);

        // Implémentation basique - à adapter selon votre modèle Client
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);

        addTableHeader(table, "Client");
        addTableHeader(table, "Email");
        addTableHeader(table, "Commandes");
        addTableHeader(table, "Montant total");

        // Exemple de données (remplacer par vos données réelles)
        table.addCell("Client 1");
        table.addCell("client1@example.com");
        table.addCell("3");
        table.addCell("150.00 €");

        document.add(table);
        document.add(Chunk.NEWLINE);
    }

    private void addReordersSection(Document document) throws Exception {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLUE);
        Paragraph sectionTitle = new Paragraph("4. COMMANDES DE RÉAPPROVISIONNEMENT", sectionFont);
        sectionTitle.setSpacingAfter(15);
        document.add(sectionTitle);

        List<Commande> reorders = CommandeDAO.getCommandesReapprovisionnement();

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);

        addTableHeader(table, "N° Commande");
        addTableHeader(table, "Date");
        addTableHeader(table, "Produit");
        addTableHeader(table, "Quantité");
        addTableHeader(table, "Statut");

        for (Commande cmd : reorders) {
            for (Commande.LigneCommande ligne : cmd.getLignesCommande()) {
                table.addCell(cmd.getId().toString());
                table.addCell(new SimpleDateFormat("dd/MM/yyyy").format(cmd.getDateCommande()));
                table.addCell(ligne.getProduit().getNom());
                table.addCell(String.valueOf(ligne.getQuantite()));
                table.addCell(cmd.isEstLivree() ? "Livrée" : "En attente");
            }
        }

        document.add(table);
        document.add(Chunk.NEWLINE);
    }

    private void addSignatureArea(Document document) throws DocumentException {
        // Espace avant la signature
        document.add(new Paragraph("\n\n\n"));

        // Tableau pour aligner la zone de signature
        PdfPTable signatureTable = new PdfPTable(1);
        signatureTable.setWidthPercentage(30);
        signatureTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

        // Ligne de signature
        PdfPCell signatureCell = new PdfPCell();
        signatureCell.setBorder(Rectangle.BOTTOM);
        signatureCell.setFixedHeight(40f); // Hauteur de la ligne de signature
        signatureCell.setPaddingBottom(10f);
        signatureTable.addCell(signatureCell);

        // Texte "Signature"
        PdfPCell textCell = new PdfPCell(new Phrase("Signature",
                FontFactory.getFont(FontFactory.HELVETICA, 10)));
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        signatureTable.addCell(textCell);

        // Date et lieu
        PdfPCell dateCell = new PdfPCell(new Phrase("Fait à ____________________, le ____/____/____",
                FontFactory.getFont(FontFactory.HELVETICA, 8)));
        dateCell.setBorder(Rectangle.NO_BORDER);
        dateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        signatureTable.addCell(dateCell);

        document.add(signatureTable);
    }

    private void addTableHeader(PdfPTable table, String header) {
        PdfPCell cell = new PdfPCell(new Phrase(header,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
        cell.setBackgroundColor(new BaseColor(70, 130, 180)); // Bleu acier
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        table.addCell(cell);
    }

    // ... (autres méthodes de la classe Admin) ...
}