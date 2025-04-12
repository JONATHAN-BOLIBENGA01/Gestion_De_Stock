package org.example.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.example.product.Produit;
import org.example.user.User;
import org.example.user.UserAdapter;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonRepository implements UserRepository, ProductRepository {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(User.class, new UserAdapter()) // Enregistrer le TypeAdapter
            .create();

    @Override
    public void saveUsers(List<User> users, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(users, writer);
            System.out.println("Données des utilisateurs sauvegardées dans " + filename);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des utilisateurs : " + e.getMessage());
        }
    }

    @Override
    public List<User> loadUsers(String filename) {
        try (FileReader reader = new FileReader(filename)) {
            return gson.fromJson(reader, new TypeToken<List<User>>(){}.getType());
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement des utilisateurs : " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void saveProducts(List<Produit> products, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(products, writer);
            System.out.println("Données des produits sauvegardées dans " + filename);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des produits : " + e.getMessage());
        }
    }

    @Override
    public List<Produit> loadProducts(String filename) {
        try (FileReader reader = new FileReader(filename)) {
            return gson.fromJson(reader, new TypeToken<List<Produit>>(){}.getType());
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement des produits : " + e.getMessage());
            return new ArrayList<>(); // Retournez une liste vide au lieu de null
        }
    }
}