package edu.escuelaing.arem.ASE.app.service;

import com.nimbusds.jose.shaded.gson.Gson;
import edu.escuelaing.arem.ASE.app.model.User;
import edu.escuelaing.arem.ASE.app.tools.Response;

import java.util.*;

public class UserService{
    public static final Map<String, User> userDatabase = new HashMap<>();
    public UserService() {
        createUsers();
    }
    void createUsers(){
        System.out.println("Usuarios Cargados");
        userDatabase.put("John", new User(UUID.randomUUID().hashCode(), "John", "john_wick@example.com"));
        userDatabase.put("Alice", new User(UUID.randomUUID().hashCode(), "Alice", "alice_smith@example.com"));
        userDatabase.put("Bob", new User(UUID.randomUUID().hashCode(), "Bob", "bob_marley@example.com"));
        userDatabase.put("Charlie", new User(UUID.randomUUID().hashCode(), "Charlie", "charlie_brown@example.com"));
        userDatabase.put("Diana", new User(UUID.randomUUID().hashCode(), "Diana", "diana_prince@example.com"));
        userDatabase.put("Eve", new User(UUID.randomUUID().hashCode(), "Eve", "eve_adams@example.com"));
    }

    public Response<String> allUsers() {
        System.out.println("Finding all Users...");
        List<User> users = new ArrayList<>(userDatabase.values());
        // Convertir la lista de usuarios a JSON
        Gson gson = new Gson();
        return new Response<String>("application/json","200","OK",null,gson.toJson(users));
    }

    public Response<String> getUser(String nombre){
        System.out.println("Finding User..."+nombre+".");
        User user = userDatabase.get(nombre);
        if (user != null) {
            // Convert user object to JSON
            return new Response<String>("application/json","200","OK",null,"{\n" +
                    "  \"name\": \"" + user.getNombre() + "\",\n" +
                    "  \"id\": " + user.getId() + ",\n" +
                    "  \"email\": \"" + user.getEmail() + "\"\n" +
                    "}");
        }
        return new Response<String>("text/html","404","USER NOT FOUND",null,null);
    }
}
