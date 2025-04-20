package org.example;

import org.example.dao.CategorieDAO;
import org.example.dao.ProduitDAO;
import org.example.product.Categorie;
import org.example.product.Commande;
import org.example.product.GestionStock;
import org.example.product.Produit;
import org.example.user.Admin;
import org.example.user.Client;
import org.example.user.User;
import org.example.user.Vendeur;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    private static GestionStock gestionStock = new GestionStock();
    private static Scanner scanner = new Scanner(System.in);
    private static User utilisateurActuel = null;

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  🎉 BIENVENUE DANS NOTRE APPLICATION  🎉 ");
        System.out.println("========================================");

        int choix;
        do {
            afficherMenuPrincipal();
            choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    inscrireUtilisateur();
                    break;
                case 2:
                    seConnecter();
                    break;
                case 0:
                    System.out.println("Merci d'avoir utilisé notre système. À bientôt !");
                    break;
                default:
                    System.out.println("Choix invalide. Réessayez.");
            }
        } while (choix != 0);
    }

    private static void afficherMenuPrincipal() {
        System.out.println("\n==== Menu Principal ====");
        System.out.println("1. S'inscrire");
        System.out.println("2. Se connecter");
        System.out.println("0. Quitter");
        System.out.print("Votre choix : ");
    }

    private static void inscrireUtilisateur() {
        System.out.println("\n=== Inscription ===");
        System.out.print("Nom : ");
        String nom = scanner.nextLine();
        System.out.print("Email : ");
        String email = scanner.nextLine();
        System.out.print("Mot de passe : ");
        String password = scanner.nextLine();

        if (User.emailExiste(email)) {
            System.out.println("\u26A0️ Cet email est déjà utilisé. Veuillez en choisir un autre.");
            return;
        }

        System.out.println("Rôle : (1) Client, (2) Vendeur, (3) Admin");
        int role = scanner.nextInt();
        scanner.nextLine();

        User nouvelUtilisateur;
        if (role == 1) {
            nouvelUtilisateur = new Client(nom, email, password);
        } else if (role == 2) {
            System.out.print("Matricule : ");
            String matricule = scanner.nextLine();
            nouvelUtilisateur = new Vendeur(nom, email, password, matricule);
        } else if (role == 3) {
            nouvelUtilisateur = new Admin(nom, email, password);
        } else {
            System.out.println("Rôle invalide. Inscription annulée.");
            return;
        }

        nouvelUtilisateur.sInscrire();
    }

    private static void seConnecter() {
        System.out.println("\n=== Connexion ===");
        System.out.print("Email : ");
        String email = scanner.nextLine();
        System.out.print("Mot de passe : ");
        String password = scanner.nextLine();

        utilisateurActuel = User.seConnecter(email, password);
        if (utilisateurActuel != null) {
            System.out.println("✅ Connexion réussie. Bienvenue, " + utilisateurActuel.getName() + "!");
            afficherMenuUtilisateur();
        } else {
            System.out.println("\u26A0️ Email ou mot de passe incorrect. Réessayez.");
        }
    }

    private static void afficherMenuUtilisateur() {
        int choix;
        do {
            System.out.println("\n==== Menu Utilisateur ====");
            System.out.println("1. Consulter les produits");
            System.out.println("2. Rechercher un produit");

            if (utilisateurActuel instanceof Client) {
                System.out.println("3. Passer une commande");
            }

            if (utilisateurActuel instanceof Vendeur || utilisateurActuel instanceof Admin) {
                System.out.println("4. Ajouter un produit");
                System.out.println("5. Mettre à jour un produit");
                System.out.println("6. Supprimer un produit");
            }

            if (utilisateurActuel instanceof Vendeur) {
                System.out.println("7. Générer une facture");
            }

            if (utilisateurActuel instanceof Admin) {
                System.out.println("8. Ajouter une catégorie");
                System.out.println("9. Gérer les commandes");
                System.out.println("10. Générer rapport PDF");
            }

            System.out.println("0. Se déconnecter");
            System.out.print("Votre choix : ");
            choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    afficherProduits();
                    break;
                case 2:
                    rechercherProduit();
                    break;
                case 3:
                    if (utilisateurActuel instanceof Client) {
                        passerCommandeClient();
                    } else {
                        System.out.println("\u26A0️ Vous n'avez pas les permissions nécessaires.");
                    }
                    break;
                case 4:
                    if (utilisateurActuel instanceof Vendeur || utilisateurActuel instanceof Admin) {
                        ajouterProduit();
                    } else {
                        System.out.println("\u26A0️ Vous n'avez pas les permissions nécessaires.");
                    }
                    break;
                case 5:
                    if (utilisateurActuel instanceof Vendeur || utilisateurActuel instanceof Admin) {
                        mettreAJourProduit();
                    } else {
                        System.out.println("\u26A0️ Vous n'avez pas les permissions nécessaires.");
                    }
                    break;
                case 6:
                    if (utilisateurActuel instanceof Vendeur || utilisateurActuel instanceof Admin) {
                        supprimerProduit();
                    } else {
                        System.out.println("\u26A0️ Vous n'avez pas les permissions nécessaires.");
                    }
                    break;
                case 7:
                    if (utilisateurActuel instanceof Vendeur) {
                        genererFacture();
                    } else {
                        System.out.println("\u26A0️ Seul un vendeur peut générer des factures.");
                    }
                    break;
                case 8:
                    if (utilisateurActuel instanceof Admin) {
                        ajouterCategorie();
                    } else {
                        System.out.println("\u26A0️ Seul un administrateur peut ajouter une catégorie.");
                    }
                    break;
                case 9:
                    if (utilisateurActuel instanceof Admin) {
                        gererCommandes();
                    } else {
                        System.out.println("\u26A0️ Seul un administrateur peut gérer les commandes.");
                    }
                    break;
                case 10:
                    if (utilisateurActuel instanceof Admin) {
                        ((Admin)utilisateurActuel).genererRapportPDF();
                    } else {
                        System.out.println("\u26A0️ Seul un administrateur peut générer des rapports.");
                    }
                    break;
                case 0:
                    System.out.println("Déconnexion réussie. Retour au menu principal.");
                    utilisateurActuel = null;
                    break;
                default:
                    System.out.println("Choix invalide. Réessayez.");
            }
        } while (choix != 0 && utilisateurActuel != null);
    }

    private static void passerCommandeClient() {
        Client client = (Client) utilisateurActuel;
        List<Produit> produits = gestionStock.afficherProduits();
        List<Produit> produitsSelectionnes = new ArrayList<>();
        List<Integer> quantites = new ArrayList<>();

        System.out.println("\n=== Passer une commande ===");
        afficherProduits();

        while (true) {
            System.out.print("\nID du produit à commander (0 pour terminer): ");
            Long id = scanner.nextLong();
            scanner.nextLine();

            if (id == 0) break;

            Produit produit = produits.stream()
                    .filter(p -> p.getId().equals(id))
                    .findFirst()
                    .orElse(null);

            if (produit == null) {
                System.out.println("Produit non trouvé");
                continue;
            }

            System.out.print("Quantité pour " + produit.getNom() + ": ");
            int quantite = scanner.nextInt();
            scanner.nextLine();

            produitsSelectionnes.add(produit);
            quantites.add(quantite);
        }

        if (!produitsSelectionnes.isEmpty()) {
            client.passerCommande(produitsSelectionnes, quantites);
        }
    }

    private static void genererFacture() {
        Vendeur vendeur = (Vendeur) utilisateurActuel;
        System.out.println("\n=== Générer une facture ===");
        System.out.print("ID de la commande: ");
        Long commandeId = scanner.nextLong();
        scanner.nextLine();

        vendeur.genererFacture(commandeId);
    }

    private static void gererCommandes() {
        Admin admin = (Admin) utilisateurActuel;
        int choix;
        do {
            System.out.println("\n==== Gestion des Commandes ====");
            System.out.println("1. Passer une commande de réapprovisionnement");
            System.out.println("2. Valider une livraison");
            System.out.println("3. Voir l'historique des commandes");
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");
            choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    passerCommandeReapprovisionnement(admin);
                    break;
                case 2:
                    validerLivraison(admin);
                    break;
                case 3:
                    afficherHistoriqueCommandes(admin);
                    break;
                case 0:
                    System.out.println("Retour au menu principal.");
                    break;
                default:
                    System.out.println("Choix invalide. Réessayez.");
            }
        } while (choix != 0);
    }

    private static void passerCommandeReapprovisionnement(Admin admin) {
        System.out.println("\n=== Commande de réapprovisionnement ===");
        Commande commande = admin.creerCommandePourProduitsSousSeuil();

        if (commande != null) {
            System.out.println("✅ Commande créée avec succès !");
            System.out.println("Détails de la commande :");
            System.out.println("ID: " + commande.getId());
            System.out.println("Date: " + commande.getDateCommande());
            System.out.println("Total: " + commande.calculerTotal() + " $");

            System.out.println("\nProduits commandés :");
            for (Commande.LigneCommande ligne : commande.getLignesCommande()) {
                System.out.println("- " + ligne.getProduit().getNom() +
                        " | Quantité: " + ligne.getQuantite() +
                        " | Prix unitaire: " + ligne.getProduit().getPrix() + " $");
            }
        } else {
            System.out.println("Aucun produit ne nécessite de réapprovisionnement pour le moment.");
        }
    }

    private static void validerLivraison(Admin admin) {
        System.out.println("\n=== Validation de livraison ===");
        System.out.print("Entrez l'ID de la commande à valider : ");
        Long commandeId = scanner.nextLong();
        scanner.nextLine();

        admin.validerLivraisonCommande(commandeId);
    }

    private static void afficherHistoriqueCommandes(Admin admin) {
        System.out.println("\n=== Historique des Commandes ===");
        List<Commande> commandes = admin.getHistoriqueCommandes();

        if (commandes.isEmpty()) {
            System.out.println("Aucune commande enregistrée.");
            return;
        }

        for (Commande cmd : commandes) {
            System.out.println("\nCommande #" + cmd.getId());
            System.out.println("Date: " + cmd.getDateCommande());
            System.out.println("Statut: " + (cmd.isEstLivree() ? "Livrée" : "En attente"));
            System.out.println("Total: " + cmd.calculerTotal() + " $");

            System.out.println("Produits :");
            for (Commande.LigneCommande ligne : cmd.getLignesCommande()) {
                System.out.println("- " + ligne.getProduit().getNom() +
                        " | Quantité: " + ligne.getQuantite() +
                        " | Prix unitaire: " + ligne.getProduit().getPrix() + " $");
            }
        }
    }

    private static void ajouterProduit() {
        System.out.print("Nom du produit : ");
        String nom = scanner.nextLine();
        System.out.print("Prix : ");
        float prix = scanner.nextFloat();
        System.out.print("Quantité en stock : ");
        int quantiteStock = scanner.nextInt();
        System.out.print("Seuil d'alerte : ");
        int seuilAlerte = scanner.nextInt();
        scanner.nextLine();

        List<Categorie> categories = CategorieDAO.getAllCategories();
        if (categories.isEmpty()) {
            System.out.println("⚠️ Aucune catégorie disponible. Créez d'abord une catégorie.");
            return;
        }

        System.out.println("=== Choisissez une catégorie ===");
        for (Categorie cat : categories) {
            System.out.println(cat.getId() + ". " + cat.getNom());
        }

        System.out.print("ID de la catégorie : ");
        Long idCategorie = scanner.nextLong();
        scanner.nextLine();

        Categorie categorie = CategorieDAO.getCategorieById(Math.toIntExact(idCategorie));
        if (categorie == null) {
            System.out.println("❌ Catégorie invalide.");
            return;
        }
        Produit nouveauProduit = new Produit(nom, prix, quantiteStock, seuilAlerte, categorie);
        gestionStock.ajouterProduit(nouveauProduit);

        if (utilisateurActuel instanceof Vendeur) {
            gestionStock.ajouterProduit(nouveauProduit, utilisateurActuel.getEmail());
        } else {
            gestionStock.ajouterProduit(nouveauProduit);
        }
        gestionStock.ajouterProduit(nouveauProduit);
    }

    private static void mettreAJourProduit() {
        System.out.print("ID du produit à mettre à jour : ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        System.out.print("Nouveau nom : ");
        String nom = scanner.nextLine();
        System.out.print("Nouveau prix : ");
        float prix = scanner.nextFloat();
        System.out.print("Nouvelle quantité en stock : ");
        int quantiteStock = scanner.nextInt();
        System.out.print("Nouveau seuil d'alerte : ");
        int seuilAlerte = scanner.nextInt();
        scanner.nextLine();

        if (gestionStock.mettreAJourProduit(id, nom, prix, quantiteStock, seuilAlerte)) {
            System.out.println("✅ Produit mis à jour !");
        } else {
            System.out.println("⚠️ Échec de la mise à jour du produit.");
        }
    }

    private static void supprimerProduit() {
        System.out.print("ID du produit à supprimer : ");
        Long id = scanner.nextLong();
        scanner.nextLine();

        gestionStock.supprimerProduit(id);
    }

    private static void afficherProduits() {
        List<Produit> produits = gestionStock.afficherProduits();
        if (produits.isEmpty()) {
            System.out.println("Aucun produit en stock.");
        } else {
            System.out.println("\n=== Liste des Produits ===");
            for (Produit p : produits) {
                System.out.println("ID: " + p.getId() + " | Nom: " + p.getNom() +
                        " | Prix: " + p.getPrix() + " | Stock: " + p.getQuantiteStock() +
                        " | Catégorie: " + (p.getCategorie() != null ? p.getCategorie().getNom() : "Aucune"));
            }
        }
    }

    private static void ajouterCategorie() {
        System.out.print("Nom de la nouvelle catégorie : ");
        String nomCategorie = scanner.nextLine();

        Categorie nouvelleCategorie = new Categorie(nomCategorie);
        CategorieDAO.saveCategorie(nouvelleCategorie);

        System.out.println("✅ Catégorie ajoutée avec succès !");
    }

    private static void rechercherProduit() {
        System.out.println("\n=== Recherche de Produit ===");
        System.out.print("Entrez le nom du produit à rechercher : ");
        String nomRecherche = scanner.nextLine();

        List<Produit> produitsTrouves = gestionStock.afficherProduits().stream()
                .filter(p -> p.getNom().toLowerCase().contains(nomRecherche.toLowerCase()))
                .collect(Collectors.toList());

        if (produitsTrouves.isEmpty()) {
            System.out.println("Aucun produit trouvé avec ce nom.");
        } else {
            System.out.println("\n=== Résultats de la recherche ===");
            for (Produit p : produitsTrouves) {
                System.out.println("ID: " + p.getId() + " | Nom: " + p.getNom() +
                        " | Prix: " + p.getPrix() + " | Stock: " + p.getQuantiteStock() +
                        " | Catégorie: " + (p.getCategorie() != null ? p.getCategorie().getNom() : "Aucune"));
            }
        }
    }
}