package org.example.user;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class UserAdapter extends TypeAdapter<User> {
    @Override
    public void write(JsonWriter out, User user) throws IOException {
        out.beginObject();
        out.name("name").value(user.getName());
        out.name("email").value(user.getEmail());
        // Ajoutez d'autres champs si nécessaire
        out.name("type").value(user instanceof Client ? "client" : "vendeur");
        out.endObject();
    }

    @Override
    public User read(JsonReader in) throws IOException {
        in.beginObject();
        String name = null;
        String email = null;
        String type = null;

        while (in.hasNext()) {
            switch (in.nextName()) {
                case "name":
                    name = in.nextString();
                    break;
                case "email":
                    email = in.nextString();
                    break;
                case "type":
                    type = in.nextString();
                    break;
            }
        }
        in.endObject();

        // Instancier le bon type d'utilisateur sans validation
        if ("client".equals(type)) {
            return new Client(name, email, "defaultPassword");
        } else if ("vendeur".equals(type)) {
            return new Vendeur(name, email, "defaultPassword", "defaultMatricule");
        }
        return null; // Retourner null si le type est inconnu
    }
}