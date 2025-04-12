package org.example.user;

import com.google.gson.Gson;
import java.util.List;

public class Client extends User {
    private Gson gson = new Gson(); // Créer une instance de Gson

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

    @Override
    public String toJson() {
        return gson.toJson(this); // Convertir l'objet en JSON
    }
}