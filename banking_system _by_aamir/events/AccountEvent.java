package events;

import model.Account;
import java.util.EventObject;

/**
 * AccountEvent.java
 * Custom event fired when account operations occur.
 * Part of the Delegation Event Model implementation.
 */
public class AccountEvent extends EventObject {

    public enum Type { OPENED, CLOSED, BALANCE_UPDATED, SELECTED }

    private final Type    type;
    private final Account account;

    public AccountEvent(Object source, Type type, Account account) {
        super(source);
        this.type    = type;
        this.account = account;
    }

    public Type getType()    { 
    return type; 
    }

    public Account getAccount(){
    return account; 
    }
}
