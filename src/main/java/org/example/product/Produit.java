package org.example.product;

import java.util.Date;

public class Produit {
    private Long id;
    private String nom;
    private float prix;
    private int quantiteStock;
    private int seuilAlerte;
    private Date dateAjout;

    public Produit(Long id, String nom, float prix, int quantiteStock, int seuilAlerte) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.quantiteStock = quantiteStock;
        this.seuilAlerte = seuilAlerte;
        this.dateAjout = new Date();
    }

    public Produit(String nom, float prix, int quantiteStock, int seuilAlerte) {
        this(null, nom, prix, quantiteStock, seuilAlerte);
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

    public boolean besoinReapprovisionnement() {
        return quantiteStock < seuilAlerte;
    }
}