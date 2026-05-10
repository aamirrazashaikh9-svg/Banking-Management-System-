package interfaces;

import events.LoginEvent;

public interface LoginListener extends java.util.EventListener {

    /** Called when login succeeds or fails, or when user logs out. */
    void onLoginStateChanged(LoginEvent event);
}
