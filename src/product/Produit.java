package product;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
public class Produit {
    private static Long nextId = 1L; // Compteur pour les IDs
    private Long id;
    private String nom;
    private float prix;
    private int quantiteStock;
    private int seuilAlerte;
    private Date dateAjout;

    public Produit(String nom, float prix, int quantiteStock, int seuilAlerte) {
        this.id = nextId++;
        this.nom = nom;
        this.prix = prix;
        this.quantiteStock = quantiteStock;
        this.seuilAlerte = seuilAlerte;
        this.dateAjout = new Date(); // Date actuelle par défaut
    }

    public float getPrix() {
        return prix;
    }

    public void setPrix(float prix) {
        this.prix = prix;
    }

    public Date getDateAjout() {
        return dateAjout;
    }

    public int getSeuilAlerte() {
        return seuilAlerte;
    }

    public void setSeuilAlerte(int seuilAlerte) {
        this.seuilAlerte = seuilAlerte;
    }

    public int getQuantiteStock() {
        return quantiteStock;
    }

    public void setQuantiteStock(int quantiteStock) {
        this.quantiteStock = quantiteStock;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Long getId() {
        return id;
    }

    public static Long getNextId() {
        return nextId;
    }
    public static void ajouterProduit(List<Produit> inventaire, Produit nouveau) {
        inventaire.add(nouveau);
    }

    public static boolean supprimerProduit(List<Produit> inventaire, Long id) {
        return inventaire.removeIf(p -> p.getId().equals(id));
    }

    public static List<Produit> rechercherParNom(List<Produit> inventaire, String terme) {
        return inventaire.stream()
                .filter(p -> p.getNom().toLowerCase().contains(terme.toLowerCase()))
                .collect(Collectors.toList());
    }

    public boolean besoinReapprovisionnement() {
        return quantiteStock < seuilAlerte;
    }
}