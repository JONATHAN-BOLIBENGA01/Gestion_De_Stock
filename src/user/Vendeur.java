package user;

import java.util.List;

public class Vendeur extends User {
    private String matricule;

    public Vendeur(String name, String email, String password, String matricule) {
        super(name, email, password);
        this.matricule = matricule;
    }

    @Override
    public void sInscrire(List<User> users) {
        System.out.println("Erreur : Un vendeur ne peut pas s'inscrire lui-même.");
    }

    public void ajouterVendeur(List<User> users, Vendeur nouveauVendeur) {
        if (emailExiste(users, nouveauVendeur.getEmail())) {
            System.out.println("Erreur : Cet email est déjà utilisé.");
            return;
        }
        users.add(nouveauVendeur);
        System.out.println("Vendeur ajouté avec succès : " + nouveauVendeur.getName());
    }
}
