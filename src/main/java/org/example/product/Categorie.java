package org.example.product;

import java.util.ArrayList;
import java.util.List;

public class Categorie {
    private Long id; // identifiant unique
    private String nom;
    private List<Produit> produits;

    public Categorie(Long id, String nom) {
        this.id = id;
        this.nom = nom;
        this.produits = new ArrayList<>();
    }

    public Categorie(String nom) {
        this(null, nom);
    }

    public Categorie() {

    }

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public List<Produit> getProduits() { return produits; }
    public void setProduits(List<Produit> produits) { this.produits = produits; }

    // Ajouter un produit à la catégorie
    public void ajouterProduit(Produit produit) {
        if (!produits.contains(produit)) {
            produits.add(produit);
        }
    }

    // Retirer un produit
    public void retirerProduit(Produit produit) {
        produits.remove(produit);
    }

    @Override
    public String toString() {
        return "Catégorie : " + nom + " (" + produits.size() + " produits)";
    }
}
