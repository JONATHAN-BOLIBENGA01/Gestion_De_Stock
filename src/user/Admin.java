package user;
import java.util.List;

class Admin extends User {
    private static Admin instance;

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
}
