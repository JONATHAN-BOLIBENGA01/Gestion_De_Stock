package org.example.user;

import org.example.product.GestionStock;
import org.example.product.Produit;

import java.util.List;

public class Vendeur extends User {
    private String matricule;

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
}