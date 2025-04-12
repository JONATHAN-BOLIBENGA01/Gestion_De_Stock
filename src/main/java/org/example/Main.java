package org.example;

import org.example.product.GestionStock;
import org.example.product.Produit;
import org.example.user.Client;
import org.example.user.User;
import org.example.user.Vendeur;
import org.example.repository.JsonRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static List<User> utilisateurs = new ArrayList<>();
    private static List<Produit> inventaire = new ArrayList<>();
    private static GestionStock gestionStock = new GestionStock();
    private static JsonRepository jsonRepository = new JsonRepository();
    private static Scanner scanner = new Scanner(System.in);
    private static User utilisateurActuel = null;

    public static void main(String[] args) {
        // Initialiser le gestionnaire de stock
        gestionStock = new GestionStock();

        System.out.println("========================================");
        System.out.println("  🎉 BIENVENUE DANS NOTRE APPLICATION  🎉 ");
        System.out.println("========================================");

        int choix;
        do {
            afficherMenuPrincipal();
            choix = scanner.nextInt();
            scanner.nextLine(); // Consommer le retour à la ligne

            switch (choix) {
                case 1:
                    inscrireUtilisateur();
                    break;
                case 2:
                    seConnecter();
                    break;
                case 0:
                    // Sauvegarder les utilisateurs et produits à la déconnexion
                    jsonRepository.saveUsers(utilisateurs, "utilisateurs.json");
                    jsonRepository.saveProducts(gestionStock.afficherProduits(), "produits.json");
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

        if (emailExiste(email)) {
            System.out.println("\u26A0️ Cet email est déjà utilisé. Veuillez en choisir un autre.");
            return;
        }

        System.out.println("Rôle : (1) Client, (2) Vendeur");
        int role = scanner.nextInt();
        scanner.nextLine();

        User nouvelUtilisateur;
        if (role == 1) {
            nouvelUtilisateur = new Client(nom, email, password);
        } else if (role == 2) {
            System.out.print("Matricule : ");
            String matricule = scanner.nextLine();
            nouvelUtilisateur = new Vendeur(nom, email, password, matricule);
        } else {
            System.out.println("Rôle invalide. Inscription annulée.");
            return;
        }

        utilisateurs.add(nouvelUtilisateur);
        System.out.println("✅ Inscription réussie ! Vous pouvez maintenant vous connecter.");
    }

    private static boolean emailExiste(String email) {
        for (User user : utilisateurs) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    private static void seConnecter() {
        System.out.println("\n=== Connexion ===");
        System.out.print("Email : ");
        String email = scanner.nextLine();
        System.out.print("Mot de passe : ");
        String password = scanner.nextLine();

        for (User user : utilisateurs) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                utilisateurActuel = user;
                System.out.println("✅ Connexion réussie. Bienvenue, " + user.getName() + "!");
                afficherMenuUtilisateur();
                return;
            }
        }
        System.out.println("\u26A0️ Email ou mot de passe incorrect. Réessayez.");
    }

    private static void afficherMenuUtilisateur() {
        int choix;
        do {
            System.out.println("\n==== Menu Utilisateur ====");
            System.out.println("1. Consulter les produits");
            if (utilisateurActuel instanceof Vendeur) {
                System.out.println("2. Ajouter un produit");
                System.out.println("3. Mettre à jour un produit");
                System.out.println("4. Supprimer un produit");
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
                    if (utilisateurActuel instanceof Vendeur) {
                        ajouterProduit();
                    } else {
                        System.out.println("\u26A0️ Vous n'avez pas les permissions pour ajouter un produit.");
                    }
                    break;
                case 3:
                    if (utilisateurActuel instanceof Vendeur) {
                        mettreAJourProduit();
                    } else {
                        System.out.println("\u26A0️ Vous n'avez pas les permissions pour mettre à jour un produit.");
                    }
                    break;
                case 4:
                    if (utilisateurActuel instanceof Vendeur) {
                        supprimerProduit();
                    } else {
                        System.out.println("\u26A0️ Vous n'avez pas les permissions pour supprimer un produit.");
                    }
                    break;
                case 0:
                    System.out.println("Déconnexion réussie. Retour au menu principal.");
                    utilisateurActuel = null;
                    break;
                default:
                    System.out.println("Choix invalide. Réessayez.");
            }
        } while (choix != 0);
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

        Produit nouveauProduit = new Produit(nom, prix, quantiteStock, seuilAlerte);
        gestionStock.ajouterProduit(nouveauProduit);

        // Sauvegarder les produits après ajout
        jsonRepository.saveProducts(gestionStock.afficherProduits(), "produits.json");
    }

    private static void mettreAJourProduit() {
        System.out.print("ID du produit à mettre à jour : ");
        Long id = scanner.nextLong();
        scanner.nextLine(); // Consommer le retour à la ligne
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
        scanner.nextLine(); // Consommer le retour à la ligne

        gestionStock.supprimerProduit(id);
    }

    private static void afficherProduits() {
        List<Produit> produits = gestionStock.afficherProduits();
        if (produits.isEmpty()) {
            System.out.println("Aucun produit en stock.");
        } else {
            System.out.println("\n=== Liste des Produits ===");
            for (Produit p : produits) {
                System.out.println("ID: " + p.getId() + " | Nom: " + p.getNom() + " | Prix: " + p.getPrix() + " | Stock: " + p.getQuantiteStock());
            }
        }
    }
}