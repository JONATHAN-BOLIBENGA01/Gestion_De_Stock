package org.example.user;

import com.google.gson.Gson;
import java.util.List;

public class Vendeur extends User {
    private String matricule;
    private Gson gson = new Gson(); // Créer une instance de Gson

    public Vendeur(String name, String email, String password, String matricule) {
        super(name, email, password);
        this.matricule = matricule;
    }

    @Override
    public void sInscrire(List<User> users) {
        if (emailExiste(users, this.email)) {
            System.out.println("Erreur : Cet email est déjà utilisé.");
            return;
        }
        users.add(this);
        System.out.println("Inscription réussie pour le vendeur : " + this.name);
    }

    @Override
    public String toJson() {
        return gson.toJson(this); // Convertir l'objet en JSON
    }
}