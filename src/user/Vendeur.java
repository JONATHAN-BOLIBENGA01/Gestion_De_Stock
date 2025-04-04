package user;
public class Vendeur extends User {
    private String matricule;
    public Vendeur(String name, String email, String password, String matricule) {
        super(name, email, password);
        this.matricule = matricule;
    }
}
