package org.example.product;

import java.util.Date;

public class Produit {
    private Long id;
    private String nom;
    private float prix;
    private int quantiteStock;
    private Categorie categorie;
    private int seuilAlerte;
    private Date dateAjout;

    public Produit(Long id, String nom, float prix, int quantiteStock, int seuilAlerte, Categorie categorie) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.quantiteStock = quantiteStock;
        this.categorie = categorie;
        this.seuilAlerte = seuilAlerte;
        this.dateAjout = new Date();
    }

    public Produit(String nom, float prix, int quantiteStock, int seuilAlerte, Categorie categorie) {
        this(null, nom, prix, quantiteStock, seuilAlerte, categorie);
    }

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public float getPrix() { return prix; }
    public void setPrix(float prix) { this.prix = prix; }
    public int getQuantiteStock() { return quantiteStock; }
    public void setQuantiteStock(int quantiteStock) { this.quantiteStock = quantiteStock; }
    public int getSeuilAlerte() { return seuilAlerte; }
    public void setSeuilAlerte(int seuilAlerte) { this.seuilAlerte = seuilAlerte; }
    public Date getDateAjout() { return dateAjout; }
    public void setDateAjout(Date dateAjout) { this.dateAjout = dateAjout; }
    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public boolean besoinReapprovisionnement() {
        return quantiteStock < seuilAlerte;
    }
}
