package org.example.user;

import org.example.product.GestionStock;
import org.example.product.Produit;

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
}