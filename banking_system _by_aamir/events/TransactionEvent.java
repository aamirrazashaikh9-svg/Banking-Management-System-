package events;
import java.util.EventObject;
import model.Transaction;

public class TransactionEvent extends EventObject {

    public enum Type { DEPOSIT, WITHDRAWAL, TRANSFER, FAILED }

    private final Type type;
    private final Transaction transaction;
    private final String message;

    public TransactionEvent(Object source, Type type, Transaction transaction, String message) {
        super(source);
        this.type        = type;
        this.transaction = transaction;
        this.message     = message;
    }

    public Type getType(){
    return type; 
    }

    public Transaction getTransaction(){
    return transaction; 
    }

    public String getMessage(){ 
    return message; 
    }
}
