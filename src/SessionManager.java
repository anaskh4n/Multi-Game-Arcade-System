
public class SessionManager {
    private static SessionManager instance;
    private String username;
    private boolean admin;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public void login(String username, boolean isAdmin) {
        this.username = username;
        this.admin = isAdmin;
    }

    public void logout() { username = null; admin = false; }

    public String getUsername() { return username != null ? username : "Guest"; }
    public boolean isAdmin()    { return admin; }
}