package org.example.user;

import org.example.dao.CommandeDAO;
import org.example.product.Commande;
import org.example.product.GestionStock;
import org.example.product.Produit;

import java.text.SimpleDateFormat;
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
    public void genererFacture(Long commandeId) {
        try {
            // Récupérer les commandes associées à ce vendeur
            List<Commande> commandes = CommandeDAO.getCommandesByVendeur(this.getEmail());

            // Trouver la commande spécifique
            Commande commande = commandes.stream()
                    .filter(c -> c.getId().equals(commandeId))
                    .findFirst()
                    .orElse(null);

            if (commande == null || commande.getLignesCommande().isEmpty()) {
                System.out.println("Commande non trouvée ou vous n'avez pas les droits");
                return;
            }

            // Génération de la facture
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            System.out.println("\n=== FACTURE ===");
            System.out.println("Numéro: " + commande.getId());
            System.out.println("Date: " + sdf.format(commande.getDateCommande()));
            System.out.println("Vendeur: " + this.getName() + " (" + this.getMatricule() + ")");
            System.out.println("\nProduits:");

            double total = 0;
            for (Commande.LigneCommande ligne : commande.getLignesCommande()) {
                double prixLigne = ligne.getQuantite() * ligne.getProduit().getPrix();
                System.out.printf("- %-20s x%-5d à %-8.2f€ = %8.2f€\n",
                        ligne.getProduit().getNom(),
                        ligne.getQuantite(),
                        ligne.getProduit().getPrix(),
                        prixLigne);
                total += prixLigne;
            }

            System.out.printf("\nTOTAL: %47.2f €\n", total);
            System.out.println("================================================");
        } catch (Exception e) {
            System.out.println("Erreur lors de la génération de la facture: " + e.getMessage());
        }
    }
}