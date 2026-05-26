public class User {

    private String username;
    private String password;
    private String role;
    private boolean isBanned;

    public User(String username, String password, String role, boolean isBanned) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.isBanned = isBanned;
    }

    // Getters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole()     { return role; }
    public boolean isBanned()   { return isBanned; }

    // Setters
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role)         { this.role = role; }
    public void setBanned(boolean banned)    { this.isBanned = banned; }

    // Converts this User to a single comma-separated line for file storage
    public String toFileString() {
        return username + "," + password + "," + role + "," + isBanned;
    }

    // Reads a comma-separated line and builds a User object from it
    public static User fromFileString(String line) {
        String[] parts = line.split(",");
        String username = parts[0];
        String password = parts[1];
        String role     = parts[2];
        boolean isBanned = Boolean.parseBoolean(parts[3]);
        return new User(username, password, role, isBanned);
    }
}
