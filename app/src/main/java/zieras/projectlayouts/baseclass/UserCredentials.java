package zieras.projectlayouts.baseclass;

/**
 * Created by zieras on 22/12/2015.
 */
public class UserCredentials {
    String username, password, role;

    public UserCredentials(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public String getUsername() {return username;}
    public String getPassword() {return password;}
    public String getRole() {return role;}
}
