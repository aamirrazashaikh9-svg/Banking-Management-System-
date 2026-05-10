package events;
import java.util.EventObject;
import model.Customer;

public class CustomerEvent extends EventObject {

    public enum Type { ADDED, UPDATED, DELETED, SELECTED, CLEARED }

    private final Type type;
    private final Customer customer;

    public CustomerEvent(Object source, Type type, Customer customer) {
        super(source);
        this.type     = type;
        this.customer = customer;
    }

    public Type getType(){
    return type; 
    }

    public Customer getCustomer(){ 
    return customer; 
    }
}
