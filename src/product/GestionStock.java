package product;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GestionStock {
    private List<Produit> inventaire;

    public GestionStock() {
        this.inventaire = new ArrayList<>();
    }
    public void ajouterProduit(Produit p) {
        Produit.ajouterProduit(inventaire, p);
    }
    public List<Produit> getProduitsEnAlerte() {
        return inventaire.stream()
                .filter(Produit::besoinReapprovisionnement)
                .collect(Collectors.toList());
    }
    public List<Produit> rechercherProduits(String terme) {
        return Produit.rechercherParNom(inventaire, terme);
    }
}
