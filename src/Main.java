import product.GestionStock;
import product.Produit;

public class Main {
    public static void main(String[] args) {
        GestionStock stock = new GestionStock();

        // Ajout de produits
        stock.ajouterProduit(new Produit("Aspirine", 5.99f, 50, 10));
        stock.ajouterProduit(new Produit("Bandage", 3.49f, 8, 15));

        // Vérification des alertes
        System.out.println("Produits à réapprovisionner :");
        stock.getProduitsEnAlerte().forEach(p ->
                System.out.println(p.getNom() + " - Stock: " + p.getQuantiteStock())
        );

        // Recherche
        System.out.println("\nRésultats pour 'ban':");
        stock.rechercherProduits("ban").forEach(p ->
                System.out.println(p.getNom())
        );
    }
    }