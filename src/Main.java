

import product.Produit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static List<Produit> stock = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int choix;
        do {
            afficherMenu();
            choix = scanner.nextInt();
            scanner.nextLine(); // Consommer le retour à la ligne

            switch (choix) {
                case 1:
                    ajouterProduit();
                    break;
                case 2:
                    modifierProduit();
                    break;
                case 3:
                    supprimerProduit();
                    break;
                case 4:
                    rechercherProduit();
                    break;
                case 5:
                    afficherProduits();
                    break;
                case 0:
                    System.out.println("Merci d'avoir utilisé le système !");
                    break;
                default:
                    System.out.println("Choix invalide, veuillez réessayer.");
            }
        } while (choix != 0);
    }

    private static void afficherMenu() {
        System.out.println("\n==== Menu Gestion de Produits ====");
        System.out.println("1. Ajouter un produit");
        System.out.println("2. Modifier un produit");
        System.out.println("3. Supprimer un produit");
        System.out.println("4. Rechercher un produit");
        System.out.println("5. Afficher tous les produits");
        System.out.println("0. Quitter");
        System.out.print("Votre choix : ");
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
        scanner.nextLine(); // Consommer le retour à la ligne

        Produit produit = new Produit((long) (stock.size() + 1), nom, prix, quantiteStock, seuilAlerte, new Date());
        stock.add(produit);
        System.out.println("Produit ajouté avec succès !");
    }

    private static void modifierProduit() {
        System.out.print("ID du produit à modifier : ");
        long id = scanner.nextLong();
        scanner.nextLine(); // Consommer le retour à la ligne

        Optional<Produit> produitOpt = stock.stream().filter(p -> p.getId() == id).findFirst();
        if (produitOpt.isPresent()) {
            Produit produit = produitOpt.get();
            System.out.print("Nouveau nom : ");
            String nom = scanner.nextLine();
            System.out.print("Nouveau prix : ");
            float prix = scanner.nextFloat();
            System.out.print("Nouvelle quantité en stock : ");
            int quantiteStock = scanner.nextInt();
            System.out.print("Nouveau seuil d'alerte : ");
            int seuilAlerte = scanner.nextInt();
            scanner.nextLine(); // Consommer le retour à la ligne

            produit.modifierProduit(nom, prix, quantiteStock, seuilAlerte);
            System.out.println("Produit modifié avec succès !");
        } else {
            System.out.println("Produit non trouvé.");
        }
    }

    private static void supprimerProduit() {
        System.out.print("ID du produit à supprimer : ");
        long id = scanner.nextLong();
        scanner.nextLine(); // Consommer le retour à la ligne

        boolean removed = stock.removeIf(p -> p.getId() == id);
        if (removed) {
            System.out.println("Produit supprimé avec succès !");
        } else {
            System.out.println("Produit non trouvé.");
        }
    }

    private static void rechercherProduit() {
        System.out.print("ID du produit à rechercher : ");
        long id = scanner.nextLong();
        scanner.nextLine(); // Consommer le retour à la ligne

        Optional<Produit> produit = stock.stream().filter(p -> p.getId() == id).findFirst();
        if (produit.isPresent()) {
            Produit p = produit.get();
            System.out.println("\n=== Détails du produit ===");
            System.out.println("ID : " + p.getId());
            System.out.println("Nom : " + p.getNom());
            System.out.println("Prix : " + p.getPrix());
            System.out.println("Quantité en stock : " + p.getQuantiteStock());
            System.out.println("Seuil d'alerte : " + p.getSeuilAlerte());
            System.out.println("Date d'ajout : " + p.getDateAjout());
        } else {
            System.out.println("Produit non trouvé.");
        }
    }

    private static void afficherProduits() {
        if (stock.isEmpty()) {
            System.out.println("Aucun produit en stock.");
        } else {
            System.out.println("\n=== Liste des Produits ===");
            for (Produit p : stock) {
                System.out.println("ID: " + p.getId() + " | Nom: " + p.getNom() + " | Prix: " + p.getPrix() + " | Stock: " + p.getQuantiteStock());
            }
        }
    }
}
