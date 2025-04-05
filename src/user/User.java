package user;

import java.util.List;
import java.util.Optional;

public abstract class User {
    protected String name;
    protected String email;
    protected String password;

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public String getName() {
        return name;
    }

    // Vérifie si l'email est déjà utilisé
    protected static boolean emailExiste(List<User> users, String email) {
        return users.stream().anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    public abstract void sInscrire(List<User> users);

    public static User seConnecter(List<User> users, String email, String password) {
        Optional<User> userOpt = users.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email) && user.getPassword().equals(password))
                .findFirst();

        if (userOpt.isPresent()) {
            System.out.println("Connexion réussie! Bienvenue, " + userOpt.get().getName());
            return userOpt.get();
        } else {
            System.out.println("Échec de la connexion. Vérifiez vos identifiants.");
            return null;
        }
    }
}