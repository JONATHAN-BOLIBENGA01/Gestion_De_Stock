package org.example.user;

import org.example.dao.CommandeDAO;
import org.example.dao.ProduitDAO;
import org.example.product.Commande;
import org.example.product.Produit;

import java.util.List;

public class Client extends User {
    public Client(String name, String email, String password) {
        super(name, email, password);
    }

    @Override
    public void sInscrire() {
        if (emailExiste(email)) {
            System.out.println("Erreur : Cet email est déjà utilisé.");
            return;
        }
        this.enregistrerDansBaseDeDonnees();
        System.out.println("Client inscrit.");
    }


    public void passerCommande(List<Produit> produits, List<Integer> quantites) {
        if (produits.size() != quantites.size()) {
            System.out.println("Erreur: nombre de produits et quantités ne correspondent pas");
            return;
        }

        Commande commande = new Commande(this.getEmail());

        for (int i = 0; i < produits.size(); i++) {
            Produit produit = produits.get(i);
            int quantite = quantites.get(i);

            // Vérifier le stock
            if (produit.getQuantiteStock() < quantite) {
                System.out.println("Stock insuffisant pour le produit: " + produit.getNom());
                continue;
            }

            commande.ajouterProduit(produit, quantite);

            // Mettre à jour le stock
            produit.setQuantiteStock(produit.getQuantiteStock() - quantite);
            ProduitDAO.saveProduit(produit);
        }

        if (!commande.getLignesCommande().isEmpty()) {
            CommandeDAO.saveCommande(commande);
            System.out.println("Commande passée avec succès! Numéro de commande: " + commande.getId());
        } else {
            System.out.println("Aucun produit valide dans la commande");
        }
    }
}
