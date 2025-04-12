package org.example.user;

import com.google.gson.Gson;

import java.util.List;

public class Admin extends User {
    private static Admin instance;
    private Gson gson = new Gson(); // Créer une instance de Gson

    private Admin(String name, String email, String password) {
        super(name, email, password);
    }

    public static Admin getInstance(String name, String email, String password, List<User> users) {
        if (instance == null) {
            if (emailExiste(users, email)) {
                System.out.println("Erreur : Cet email est déjà utilisé pour un autre utilisateur.");
                return null;
            }
            instance = new Admin(name, email, password);
            users.add(instance);
        }
        return instance;
    }

    @Override
    public void sInscrire(List<User> users) {
        System.out.println("Erreur : L'admin ne peut pas s'inscrire lui-même.");
    }

    @Override
    public String toJson() {
        return gson.toJson(this); // Convertir l'objet en JSON
    }
}