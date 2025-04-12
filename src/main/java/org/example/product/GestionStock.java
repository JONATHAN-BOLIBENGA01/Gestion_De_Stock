package org.example.product;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GestionStock {
    private List<Produit> inventaire;

    public GestionStock() {
        this.inventaire = new ArrayList<>();
    }

    // Créer
    public void ajouterProduit(Produit p) {
        inventaire.add(p);
        System.out.println("✅ Produit ajouté avec succès !");
    }

    // Lire
    public List<Produit> afficherProduits() {
        return inventaire;
    }

    public Produit rechercherProduitParId(Long id) {
        return inventaire.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // Mettre à jour
    public boolean mettreAJourProduit(Long id, String nom, float prix, int quantiteStock, int seuilAlerte) {
        Produit produit = rechercherProduitParId(id);
        if (produit != null) {
            produit.setNom(nom);
            produit.setPrix(prix);
            produit.setQuantiteStock(quantiteStock);
            produit.setSeuilAlerte(seuilAlerte);
            System.out.println("✅ Produit mis à jour avec succès !");
            return true;
        }
        System.out.println("⚠️ Produit non trouvé !");
        return false;
    }

    // Supprimer
    public boolean supprimerProduit(Long id) {
        boolean removed = inventaire.removeIf(p -> p.getId().equals(id));
        if (removed) {
            System.out.println("✅ Produit supprimé avec succès !");
        } else {
            System.out.println("⚠️ Produit non trouvé !");
        }
        return removed;
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
