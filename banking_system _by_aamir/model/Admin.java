package model;

public class Admin {

    public enum Role { ADMIN, TELLER }

    private int adminId;
    private String username;
    private String password;
    private Role role;

    public Admin() {}

    public Admin(int adminId, String username, String password, Role role) {
        this.adminId  = adminId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public Admin(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public int getAdminId(){
    return adminId; 
    }

    public void setAdminId(int id){ 
    this.adminId = id; 
    }

    public String getUsername(){
    return username; 
    }

    public void   setUsername(String u){
    this.username = u; 
    }

    public String getPassword(){ 
    return password; 
    }

    public void setPassword(String p){ 
    this.password = p; 
    }

    public Role getRole(){ 
    return role; 
    }

    public void setRole(Role role){ 
    this.role = role; 
    }

    @Override
    public String toString() {
        return "Admin{id=" + adminId + ", username='" + username + "', role=" + role + "}";
    }
}
