package user;

public class Admin extends User{
    private String privaleges;
    public Admin(String name, String email, String password, String privaleges) {
        super(name, email, password);
        this.privaleges = privaleges;
    }

    public String getPrivaleges() {
        return privaleges;
    }

    public void setPrivaleges(String privaleges) {
        this.privaleges = privaleges;
    }

    public void genererRapport(){}
    public void gererUtilisateur(){}
    public void gererCategorie(){}
    public void gererFournisseur(){}
}
