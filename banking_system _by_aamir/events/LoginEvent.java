package events;

import model.Admin;
import java.util.EventObject;

/**
 * LoginEvent.java
 * Event fired on login attempts.
 */
public class LoginEvent extends EventObject {

    public enum Type { SUCCESS, FAILED, LOGOUT }

    private final Type  type;
    private final Admin admin;

    public LoginEvent(Object source, Type type, Admin admin) {
        super(source);
        this.type  = type;
        this.admin = admin;
    }

    public Type  getType(){ 
    return type; 
    }

    public Admin getAdmin(){
    return admin; 
    }
}
