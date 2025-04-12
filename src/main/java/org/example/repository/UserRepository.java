package org.example.repository;
import org.example.user.User;

import java.util.List;

public interface UserRepository {
    void saveUsers(List<User> users, String filename);
    List<User> loadUsers(String filename);
}


