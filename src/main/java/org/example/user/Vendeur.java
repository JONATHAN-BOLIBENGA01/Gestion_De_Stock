package org.example.user;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.example.dao.CommandeDAO;
import org.example.dao.ProduitDAO;
import org.example.product.Commande;
import org.example.product.GestionStock;
import org.example.product.Produit;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Vendeur extends User {
    private String matricule;
    private static final AtomicInteger lastInvoiceNumber = new AtomicInteger(1000);
    private static final String LOGO_PATH = "src/main/resources/logo.jpg";

    public Vendeur(String name, String email, String password, String matricule) {
        super(name, email, password);
        this.matricule = matricule;
    }

    public String getMatricule() {
        return matricule;
    }

    @Override
    public void sInscrire() {
        if (emailExiste(email)) {
            System.out.println("Erreur : Cet email est déjà utilisé.");
            return;
        }
        this.enregistrerDansBaseDeDonnees();
        System.out.println("Vendeur inscrit.");
    }

    public void ajouterProduit(GestionStock gestionStock, Produit produit) {
        gestionStock.ajouterProduit(produit);
    }

    public void mettreAJourProduit(GestionStock gestionStock, Long id, String nom, float prix, int quantiteStock, int seuilAlerte) {
        gestionStock.mettreAJourProduit(id, nom, prix, quantiteStock, seuilAlerte);
    }

    public void supprimerProduit(GestionStock gestionStock, Long id) {
        gestionStock.supprimerProduit(id);
    }

    public void genererEtValiderFacture(Long commandeId) {
        try {
            List<Commande> commandes = CommandeDAO.getCommandesByVendeur(this.getEmail());
            Commande commande = commandes.stream()
                    .filter(c -> c.getId().equals(commandeId))
                    .findFirst()
                    .orElse(null);

            if (commande == null || commande.getLignesCommande().isEmpty()) {
                System.out.println("Commande non trouvée ou vous n'avez pas les droits");
                return;
            }

            // 1. Générer la facture PDF
            String fileName = genererFacturePDF(commande);
            System.out.println("✅ Facture PDF générée: " + fileName);

            // 2. Valider la commande et mettre à jour les stocks
            validerCommandeEtStocks(commande);

        } catch (Exception e) {
            System.out.println("❌ Erreur: " + e.getMessage());
        }
    }

    private String genererFacturePDF(Commande commande) throws Exception {
        // Numéro de facture unique (FAC-YYYYMMDD-NNNN)
        String invoiceNumber = "FAC-" + new SimpleDateFormat("yyyyMMdd-").format(new Date())
                + lastInvoiceNumber.incrementAndGet();

        Document document = new Document();
        String fileName = "factures/facture_" + invoiceNumber + ".pdf"; // Créez le dossier 'factures'
        PdfWriter.getInstance(document, new FileOutputStream(fileName));
        document.open();

        // 1. Logo d'entreprise
        try {
            Image logo = Image.getInstance(LOGO_PATH);
            logo.scaleToFit(100, 100);
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);
        } catch (Exception e) {
            System.out.println("⚠️ Logo non trouvé, continuation sans logo");
        }

        // 2. En-tête de la facture
        Font fontTitre = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE);
        Paragraph titre = new Paragraph("FACTURE " + invoiceNumber, fontTitre);
        titre.setAlignment(Element.ALIGN_CENTER);
        titre.setSpacingAfter(20);
        document.add(titre);

        // 3. Informations de base
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        document.add(new Paragraph("Date: " + sdf.format(new Date())));
        document.add(new Paragraph("Vendeur: " + this.getName() + " (" + this.matricule + ")"));

        // 4. Informations client (à adapter selon votre modèle)
        document.add(new Paragraph("Client: " + commande.getAdminEmail())); // À remplacer par les infos réelles du client
        document.add(Chunk.NEWLINE);

        // 5. Tableau des produits
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        // En-têtes du tableau
        addTableHeader(table, "Produit");
        addTableHeader(table, "Prix unitaire");
        addTableHeader(table, "Quantité");
        addTableHeader(table, "Total");

        // Remplissage du tableau
        double total = 0;
        for (Commande.LigneCommande ligne : commande.getLignesCommande()) {
            double prixLigne = ligne.getQuantite() * ligne.getProduit().getPrix();
            total += prixLigne;

            table.addCell(ligne.getProduit().getNom());
            table.addCell(String.format("%.2f €", ligne.getProduit().getPrix()));
            table.addCell(String.valueOf(ligne.getQuantite()));
            table.addCell(String.format("%.2f €", prixLigne));
        }

        document.add(table);
        document.add(Chunk.NEWLINE);

        // 6. Total
        Font fontTotal = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Paragraph totalPara = new Paragraph(String.format("Total: %.2f €", total), fontTotal);
        totalPara.setAlignment(Element.ALIGN_RIGHT);
        document.add(totalPara);

        // 7. Conditions de paiement
        document.add(new Paragraph("\nConditions de paiement:",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        document.add(new Paragraph("Paiement à 30 jours fin de mois"));
        document.add(new Paragraph("TVA non applicable, art. 293 B du CGI"));

        document.close();
        return fileName;
    }

    private void validerCommandeEtStocks(Commande commande) {
        try {
            for (Commande.LigneCommande ligne : commande.getLignesCommande()) {
                Produit produit = ligne.getProduit();
                int nouvelleQuantite = produit.getQuantiteStock() - ligne.getQuantite();

                if (nouvelleQuantite < 0) {
                    throw new Exception("Stock insuffisant pour " + produit.getNom());
                }

                produit.setQuantiteStock(nouvelleQuantite);
                ProduitDAO.saveProduit(produit);
            }
            System.out.println("✅ Stocks mis à jour avec succès");
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de la mise à jour des stocks: " + e.getMessage());
            throw new RuntimeException("Annulation de la validation de commande");
        }
    }

    private void addTableHeader(PdfPTable table, String header) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setBorderWidth(1);
        cell.setPhrase(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        table.addCell(cell);
    }
}