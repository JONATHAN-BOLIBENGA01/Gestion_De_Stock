
import product.Produit;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import user.*;
import product.Produit;
import java.util.*;

public class Main {
    private static List<User> utilisateurs = new ArrayList<>();
    private static List<Produit> inventaire = new ArrayList<>();
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
            scanner.nextLine(); // Consommer le retour à la ligne

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
        inventaire.add(nouveauProduit);
        System.out.println("✅ Produit ajouté avec succès !");
    }

    private static void afficherProduits() {
        if (inventaire.isEmpty()) {
            System.out.println("Aucun produit en stock.");
        } else {
            System.out.println("\n=== Liste des Produits ===");
            for (Produit p : inventaire) {
                System.out.println("ID: " + p.getId() + " | Nom: " + p.getNom() + " | Prix: " + p.getPrix() + " | Stock: " + p.getQuantiteStock());
            }
        }
    }
}
