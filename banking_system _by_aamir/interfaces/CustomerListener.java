package interfaces;

import events.CustomerEvent;

public interface CustomerListener extends java.util.EventListener {

    /** Called when a new customer is successfully added. */
    void onCustomerAdded(CustomerEvent event);

    /** Called when an existing customer's data is updated. */
    void onCustomerUpdated(CustomerEvent event);

    /** Called when a customer is deleted from the system. */
    void onCustomerDeleted(CustomerEvent event);

    /** Called when a customer row is selected in the table. */
    void onCustomerSelected(CustomerEvent event);
}
