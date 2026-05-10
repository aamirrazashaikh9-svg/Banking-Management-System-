package interfaces;

import events.AccountEvent;

public interface AccountListener extends java.util.EventListener {

    /** Called when a new account is opened. */
    void onAccountOpened(AccountEvent event);

    /** Called when an account is closed/deleted. */
    void onAccountClosed(AccountEvent event);

    /** Called when an account's balance changes. */
    void onBalanceUpdated(AccountEvent event);
}
