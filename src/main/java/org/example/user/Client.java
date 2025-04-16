package org.example.user;

import java.util.List;

public class Client extends User {
    public Client(String name, String email, String password) {
        super(name, email, password);
    }

    @Override
    public void sInscrire() {
        if (emailExiste(email)) {
            System.out.println("Erreur : Cet email est déjà utilisé.");
            return;
        }
        this.enregistrerDansBaseDeDonnees();
        System.out.println("Client inscrit.");
    }
}