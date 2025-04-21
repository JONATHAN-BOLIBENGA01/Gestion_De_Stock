package org.example;

import org.example.dao.CategorieDAO;
import org.example.dao.CommandeDAO;
import org.example.dao.ProduitDAO;
import org.example.product.*;
import org.example.user.*;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final GestionStock gestionStock = new GestionStock();
    private static final Scanner scanner = new Scanner(System.in);
    private static User utilisateurActuel = null;

    public static void main(String[] args) {
        afficherMessageBienvenue();

        int choix;
        do {
            afficherMenuPrincipal();
            choix = saisirChoixUtilisateur();

            switch (choix) {
                case 1 -> inscrireUtilisateur();
                case 2 -> seConnecter();
                case 0 -> System.out.println("Merci d'avoir utilisé notre système. À bientôt !");
                default -> System.out.println("Choix invalide. Réessayez.");
            }
        } while (choix != 0);

        scanner.close();
    }

    private static void afficherMessageBienvenue() {
        System.out.println("========================================");
        System.out.println("  🎉 BIENVENUE DANS NOTRE APPLICATION  🎉 ");
        System.out.println("========================================");
    }

    private static void afficherMenuPrincipal() {
        System.out.println("\n==== Menu Principal ====");
        System.out.println("1. S'inscrire");
        System.out.println("2. Se connecter");
        System.out.println("0. Quitter");
        System.out.print("Votre choix : ");
    }

    private static int saisirChoixUtilisateur() {
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            scanner.nextLine();
            return -1;
        } finally {
            scanner.nextLine();
        }
    }

    private static void inscrireUtilisateur() {
        System.out.println("\n=== Inscription ===");
        String nom = saisirInformation("Nom : ");
        String email = saisirInformation("Email : ");
        String password = saisirInformation("Mot de passe : ");

        if (User.emailExiste(email)) {
            System.out.println("\u26A0️ Cet email est déjà utilisé. Veuillez en choisir un autre.");
            return;
        }

        System.out.println("Rôle : (1) Client, (2) Vendeur, (3) Admin");
        int role = saisirChoixUtilisateur();

        User nouvelUtilisateur = creerUtilisateurSelonRole(nom, email, password, role);
        if (nouvelUtilisateur != null) {
            nouvelUtilisateur.sInscrire();
        }
    }

    private static String saisirInformation(String message) {
        System.out.print(message);
        return scanner.nextLine();
    }

    private static User creerUtilisateurSelonRole(String nom, String email, String password, int role) {
        return switch (role) {
            case 1 -> new Client(nom, email, password);
            case 2 -> {
                System.out.print("Matricule : ");
                String matricule = scanner.nextLine();
                yield new Vendeur(nom, email, password, matricule);
            }
            case 3 -> new Admin(nom, email, password);
            default -> {
                System.out.println("Rôle invalide. Inscription annulée.");
                yield null;
            }
        };
    }

    private static void seConnecter() {
        System.out.println("\n=== Connexion ===");
        String email = saisirInformation("Email : ");
        String password = saisirInformation("Mot de passe : ");

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
            afficherOptionsMenuUtilisateur();
            choix = saisirChoixUtilisateur();
            traiterChoixUtilisateur(choix);
        } while (choix != 0 && utilisateurActuel != null);
    }

    private static void afficherOptionsMenuUtilisateur() {
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
            System.out.println("7. Générer et valider une facture");
        }

        if (utilisateurActuel instanceof Admin) {
            System.out.println("8. Ajouter une catégorie");
            System.out.println("9. Gérer les commandes");
            System.out.println("10. Générer rapport PDF");
        }

        System.out.println("0. Se déconnecter");
        System.out.print("Votre choix : ");
    }

    private static void traiterChoixUtilisateur(int choix) {
        switch (choix) {
            case 1 -> afficherProduits();
            case 2 -> rechercherProduit();
            case 3 -> {
                if (utilisateurActuel instanceof Client) {
                    passerCommandeClient();
                } else {
                    afficherMessagePermissionInsuffisante();
                }
            }
            case 4 -> {
                if (utilisateurActuel instanceof Vendeur || utilisateurActuel instanceof Admin) {
                    ajouterProduit();
                } else {
                    afficherMessagePermissionInsuffisante();
                }
            }
            case 5 -> {
                if (utilisateurActuel instanceof Vendeur || utilisateurActuel instanceof Admin) {
                    mettreAJourProduit();
                } else {
                    afficherMessagePermissionInsuffisante();
                }
            }
            case 6 -> {
                if (utilisateurActuel instanceof Vendeur || utilisateurActuel instanceof Admin) {
                    supprimerProduit();
                } else {
                    afficherMessagePermissionInsuffisante();
                }
            }
            case 7 -> {
                if (utilisateurActuel instanceof Vendeur) {
                    genererFacture();
                } else {
                    System.out.println("\u26A0️ Seul un vendeur peut générer des factures.");
                }
            }
            case 8 -> {
                if (utilisateurActuel instanceof Admin) {
                    ajouterCategorie();
                } else {
                    System.out.println("\u26A0️ Seul un administrateur peut ajouter une catégorie.");
                }
            }
            case 9 -> {
                if (utilisateurActuel instanceof Admin) {
                    gererCommandes();
                } else {
                    System.out.println("\u26A0️ Seul un administrateur peut gérer les commandes.");
                }
            }
            case 10 -> {
                if (utilisateurActuel instanceof Admin) {
                    ((Admin)utilisateurActuel).genererRapportCompletPDF();
                } else {
                    System.out.println("\u26A0️ Seul un administrateur peut générer des rapports.");
                }
            }
            case 0 -> {
                System.out.println("Déconnexion réussie. Retour au menu principal.");
                utilisateurActuel = null;
            }
            default -> System.out.println("Choix invalide. Réessayez.");
        }
    }

    private static void afficherMessagePermissionInsuffisante() {
        System.out.println("\u26A0️ Vous n'avez pas les permissions nécessaires.");
    }

    // Méthodes pour les clients
    private static void passerCommandeClient() {
        Client client = (Client) utilisateurActuel;
        List<Produit> produits = gestionStock.afficherProduits();
        List<Produit> produitsSelectionnes = new ArrayList<>();
        List<Integer> quantites = new ArrayList<>();

        System.out.println("\n=== Passer une commande ===");
        afficherProduits();

        while (true) {
            System.out.print("\nID du produit à commander (0 pour terminer): ");
            try {
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
            } catch (InputMismatchException e) {
                System.out.println("Veuillez entrer un nombre valide");
                scanner.nextLine();
            }
        }

        if (!produitsSelectionnes.isEmpty()) {
            client.passerCommande(produitsSelectionnes, quantites);
        }
    }

    // Méthodes pour les vendeurs
    private static void genererFacture() {
        Vendeur vendeur = (Vendeur) utilisateurActuel;
        System.out.println("\n=== Générer et valider une facture ===");
        System.out.print("ID de la commande: ");
        try {
            Long commandeId = scanner.nextLong();
            scanner.nextLine();
            vendeur.genererEtValiderFacture(commandeId);
        } catch (InputMismatchException e) {
            System.out.println("ID invalide");
            scanner.nextLine();
        }
    }

    // Méthodes pour les administrateurs
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

            choix = saisirChoixUtilisateur();

            switch (choix) {
                case 1 -> passerCommandeReapprovisionnement(admin);
                case 2 -> validerLivraison(admin);
                case 3 -> afficherHistoriqueCommandes(admin);
                case 0 -> System.out.println("Retour au menu principal.");
                default -> System.out.println("Choix invalide. Réessayez.");
            }
        } while (choix != 0);
    }

    private static void passerCommandeReapprovisionnement(Admin admin) {
        Commande commande = admin.creerCommandePourProduitsSousSeuil();
        if (commande != null) {
            System.out.println("✅ Commande créée avec succès !");
            afficherDetailsCommande(commande);
        } else {
            System.out.println("Aucun produit ne nécessite de réapprovisionnement pour le moment.");
        }
    }

    private static void validerLivraison(Admin admin) {
        System.out.print("Entrez l'ID de la commande à valider : ");
        try {
            Long commandeId = scanner.nextLong();
            scanner.nextLine();
            admin.validerLivraisonCommande(commandeId);
        } catch (InputMismatchException e) {
            System.out.println("Erreur : ID invalide.");
            scanner.nextLine();
        }
    }

    private static void afficherHistoriqueCommandes(Admin admin) {
        List<Commande> commandes = CommandeDAO.getCommandesByAdmin(admin.getEmail());
        if (commandes.isEmpty()) {
            System.out.println("Aucune commande enregistrée.");
            return;
        }
        commandes.forEach(Main::afficherDetailsCommande);
    }

    private static void afficherDetailsCommande(Commande commande) {
        System.out.println("\nCommande #" + commande.getId());
        System.out.println("Date: " + commande.getDateCommande());
        System.out.println("Statut: " + (commande.isEstLivree() ? "Livrée" : "En attente"));
        System.out.println("Total: " + commande.calculerTotal() + " €");

        System.out.println("Produits :");
        commande.getLignesCommande().forEach(ligne ->
                System.out.printf("- %s | Quantité: %d | Prix unitaire: %.2f €%n",
                        ligne.getProduit().getNom(),
                        ligne.getQuantite(),
                        ligne.getProduit().getPrix())
        );
    }

    private static void ajouterCategorie() {
        System.out.print("Nom de la nouvelle catégorie : ");
        String nomCategorie = scanner.nextLine().trim();

        if (nomCategorie.isEmpty()) {
            System.out.println("Erreur : le nom ne peut pas être vide.");
            return;
        }

        Categorie nouvelleCategorie = new Categorie(nomCategorie);
        boolean success = CategorieDAO.saveCategorie(nouvelleCategorie);

        if (success) {
            System.out.println("✅ Catégorie ajoutée avec succès !");
        } else {
            boolean b = false;
            System.out.println("Erreur lors de l'ajout de la catégorie.");
        }
    }

    // Méthodes pour la gestion des produits
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
        categories.forEach(cat -> System.out.println(cat.getId() + ". " + cat.getNom()));

        System.out.print("ID de la catégorie : ");
        Long idCategorie = scanner.nextLong();
        scanner.nextLine();

        Categorie categorie = CategorieDAO.getCategorieById(Math.toIntExact(idCategorie));
        if (categorie == null) {
            System.out.println("❌ Catégorie invalide.");
            return;
        }

        Produit nouveauProduit = new Produit(nom, prix, quantiteStock, seuilAlerte, categorie);

        if (utilisateurActuel instanceof Vendeur) {
            gestionStock.ajouterProduit(nouveauProduit, utilisateurActuel.getEmail());
        } else {
            gestionStock.ajouterProduit(nouveauProduit);
        }

        System.out.println("✅ Produit ajouté avec succès !");
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

        if (gestionStock.supprimerProduit(id)) {
            System.out.println("✅ Produit supprimé !");
        } else {
            System.out.println("⚠️ Échec de la suppression du produit.");
        }
    }

    private static void afficherProduits() {
        List<Produit> produits = gestionStock.afficherProduits();
        if (produits.isEmpty()) {
            System.out.println("Aucun produit en stock.");
        } else {
            System.out.println("\n=== Liste des Produits ===");
            produits.forEach(p -> System.out.printf(
                    "ID: %d | Nom: %s | Prix: %.2f € | Stock: %d | Catégorie: %s%n",
                    p.getId(),
                    p.getNom(),
                    p.getPrix(),
                    p.getQuantiteStock(),
                    p.getCategorie() != null ? p.getCategorie().getNom() : "Aucune"
            ));
        }
    }

    private static void rechercherProduit() {
        System.out.print("Entrez le nom du produit à rechercher : ");
        String nomRecherche = scanner.nextLine().toLowerCase();

        List<Produit> produitsTrouves = gestionStock.afficherProduits().stream()
                .filter(p -> p.getNom().toLowerCase().contains(nomRecherche))
                .toList();

        if (produitsTrouves.isEmpty()) {
            System.out.println("Aucun produit trouvé avec ce nom.");
        } else {
            System.out.println("\n=== Résultats de la recherche ===");
            produitsTrouves.forEach(p -> System.out.printf(
                    "ID: %d | Nom: %s | Prix: %.2f € | Stock: %d%n",
                    p.getId(), p.getNom(), p.getPrix(), p.getQuantiteStock()
            ));
        }
    }
}