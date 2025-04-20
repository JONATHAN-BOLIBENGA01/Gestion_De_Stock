package org.example.product;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Commande {
    private Long id;

    public void setLignesCommande(List<LigneCommande> lignesCommande) {
        this.lignesCommande = lignesCommande;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public void setEstLivree(boolean estLivree) {
        this.estLivree = estLivree;
    }

    public void setDateCommande(Date dateCommande) {
        this.dateCommande = dateCommande;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private List<LigneCommande> lignesCommande;
    private Date dateCommande;
    private boolean estLivree;
    private String adminEmail;

    public Commande(String adminEmail) {
        this.lignesCommande = new ArrayList<>();
        this.dateCommande = new Date();
        this.estLivree = false;
        this.adminEmail = adminEmail;
    }

    public void ajouterProduit(Produit produit, int quantite) {
        this.lignesCommande.add(new LigneCommande(produit, quantite));
    }

    public void validerCommande() {
        // Logique de validation de la commande
        this.estLivree = true;
    }

    public double calculerTotal() {
        return lignesCommande.stream()
                .mapToDouble(l -> l.getProduit().getPrix() * l.getQuantite())
                .sum();
    }

    // Getters
    public Long getId() { return id; }
    public List<LigneCommande> getLignesCommande() { return lignesCommande; }
    public Date getDateCommande() { return dateCommande; }
    public boolean isEstLivree() { return estLivree; }
    public String getAdminEmail() { return adminEmail; }

    // Classe interne pour représenter une ligne de commande
    public static class LigneCommande {
        private Produit produit;
        private int quantite;

        public LigneCommande(Produit produit, int quantite) {
            this.produit = produit;
            this.quantite = quantite;
        }

        // Getters
        public Produit getProduit() { return produit; }
        public int getQuantite() { return quantite; }
    }
}