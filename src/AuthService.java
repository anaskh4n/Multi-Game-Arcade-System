import java.util.*;

public class AuthService {

    private static final String FILE = "users.txt";

    // Returns all users from the file as User objects
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        for (String line : FileHandler.readLines(FILE)) {
            if (!line.trim().isEmpty()) {
                users.add(User.fromFileString(line));
            }
        }
        return users;
    }

    public boolean updatePassword(String username, String currentPass, String newPass) {
    List<User> users = getAllUsers();
    for (User u : users) {
        if (u.getUsername().equals(username) && u.getPassword().equals(currentPass)) {
            u.setPassword(newPass);
            saveAllUsers(users);
            return true;
        }
    }
    return false;
    }

    public boolean registerAdmin(String username, String password) {
    for (User u : getAllUsers()) {
        if (u.getUsername().equals(username)) return false;
    }
    FileHandler.appendLine(FILE, new User(username, password, "admin", false).toFileString());
    return true;
    }

    // Saves the full list of users back to the file
    private void saveAllUsers(List<User> users) {
        List<String> lines = new ArrayList<>();
        for (User u : users) {
            lines.add(u.toFileString());
        }
        FileHandler.writeLines(FILE, lines);
    }

    // Registers a new user — returns false if username already taken
    public boolean register(String username, String password) {
        for (User u : getAllUsers()) {
            if (u.getUsername().equals(username)) {
                return false; // username already exists
            }
        }
        FileHandler.appendLine(FILE, new User(username, password, "user", false).toFileString());
        return true;
    }

    // Returns the User if login is valid, otherwise null
    public User login(String username, String password) {
        for (User u : getAllUsers()) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    // Bans a user by username
    public boolean banUser(String username) {
        List<User> users = getAllUsers();
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                u.setBanned(true);
                saveAllUsers(users);
                return true;
            }
        }
        return false;
    }

    // Unbans a user by username
    
    public boolean unbanUser(String username) {
        List<User> users = getAllUsers();
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                u.setBanned(false);
                saveAllUsers(users);
                return true;
            }
        }
        return false;
    }

    // Deletes a user from the file entirely
    public boolean deleteUser(String username) {
        List<User> users = getAllUsers();
        boolean found = users.removeIf(u -> u.getUsername().equals(username));
        if (found) {
            saveAllUsers(users);
        }
        return found;
    }
}
