package user;

import java.util.List;

public class Client extends User {
    public Client(String name, String email, String password) {
        super(name, email, password);
    }

    @Override
    public void sInscrire(List<User> users) {
        if (emailExiste(users, this.email)) {
            System.out.println("Erreur : Cet email est déjà utilisé.");
            return;
        }
        users.add(this);
        System.out.println("Inscription réussie pour le client : " + this.name);
    }
}
