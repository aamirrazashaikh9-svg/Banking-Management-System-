package interfaces;

import events.TransactionEvent;

public interface TransactionListener extends java.util.EventListener {

    /** Called when any transaction completes (deposit, withdrawal, or transfer). */
    void onTransactionCompleted(TransactionEvent event);

    /** Called when a transaction fails. */
    void onTransactionFailed(TransactionEvent event);
}
