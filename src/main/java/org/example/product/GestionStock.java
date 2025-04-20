package org.example.product;

import org.example.dao.ProduitDAO;

import java.util.List;
import java.util.stream.Collectors;

public class GestionStock {
    private List<Produit> inventaire;

    public GestionStock() {
        this.inventaire = ProduitDAO.getAllProduits();
    }

    public void ajouterProduit(Produit p) {
        ajouterProduit(p, null);
    }

    public void ajouterProduit(Produit p, String vendeurEmail) {
        if (vendeurEmail != null) {
            ProduitDAO.saveProduit(p, vendeurEmail);
        } else {
            ProduitDAO.saveProduit(p);
        }
        this.inventaire = ProduitDAO.getAllProduits();
        System.out.println("✅ Produit ajouté avec succès !");
    }

    public List<Produit> afficherProduits() {
        return inventaire;
    }

    public Produit rechercherProduitParId(Long id) {
        return ProduitDAO.getProduitById(id);
    }

    public boolean mettreAJourProduit(Long id, String nom, float prix, int quantiteStock, int seuilAlerte) {
        Produit produit = rechercherProduitParId(id);
        if (produit != null) {
            produit.setNom(nom);
            produit.setPrix(prix);
            produit.setQuantiteStock(quantiteStock);
            produit.setSeuilAlerte(seuilAlerte);

            // Récupérer l'email du vendeur existant
            String vendeurEmail = ProduitDAO.getVendeurEmailForProduit(id);
            if (vendeurEmail != null) {
                ProduitDAO.saveProduit(produit, vendeurEmail);
            } else {
                ProduitDAO.saveProduit(produit);
            }

            this.inventaire = ProduitDAO.getAllProduits();
            System.out.println("✅ Produit mis à jour avec succès !");
            return true;
        }
        System.out.println("⚠️ Produit non trouvé !");
        return false;
    }

    public boolean supprimerProduit(Long id) {
        boolean success = ProduitDAO.deleteProduit(id);
        if (success) {
            this.inventaire = ProduitDAO.getAllProduits();
            System.out.println("✅ Produit supprimé avec succès !");
        } else {
            System.out.println("⚠️ Produit non trouvé !");
        }
        return success;
    }

    public List<Produit> getProduitsEnAlerte() {
        return inventaire.stream()
                .filter(Produit::besoinReapprovisionnement)
                .collect(Collectors.toList());
    }

    public List<Produit> rechercherProduits(String terme) {
        return inventaire.stream()
                .filter(p -> p.getNom().toLowerCase().contains(terme.toLowerCase()))
                .collect(Collectors.toList());
    }
}
