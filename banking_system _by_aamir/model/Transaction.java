package model;

import java.time.LocalDateTime;

public class Transaction {

    public enum TransactionType { DEPOSIT, WITHDRAWAL, TRANSFER }

    private int transactionId;
    private int fromAccountId;
    private int toAccountId;
    private int adminId;
    private TransactionType type;
    private double amount;
    private LocalDateTime dateTime;

    public Transaction() {}

    public Transaction(int transactionId, int fromAccountId, int toAccountId,
                       int adminId, TransactionType type, double amount, LocalDateTime dateTime) {
        this.transactionId = transactionId;
        this.fromAccountId = fromAccountId;
        this.toAccountId   = toAccountId;
        this.adminId       = adminId;
        this.type          = type;
        this.amount        = amount;
        this.dateTime      = dateTime;
    }

    public Transaction(int fromAccountId, int toAccountId, int adminId,
                       TransactionType type, double amount) {
        this.fromAccountId = fromAccountId;
        this.toAccountId   = toAccountId;
        this.adminId       = adminId;
        this.type          = type;
        this.amount        = amount;
        this.dateTime      = LocalDateTime.now();
    }

    public int getTransactionId(){ 
    return transactionId; 
    }

    public void setTransactionId(int id){ 
    this.transactionId = id;
    }

    public int getFromAccountId(){ 
    return fromAccountId; 
    }

    public void setFromAccountId(int id){ 
    this.fromAccountId = id;
    }

    public int getToAccountId(){
    return toAccountId; 
    }

    public void setToAccountId(int id){ 
    this.toAccountId = id; 
    }

    public int mgetAdminId(){ 
    return adminId; 
    }

    public void setAdminId(int id){ 
    this.adminId = id; 
    }

    public TransactionType getType(){
    return type; 
    }

    public void setType(TransactionType type){ 
    this.type = type; 
    }

    public double getAmount(){
    return amount; 
    }

    public void setAmount(double amount){ 
    this.amount = amount; 
    }

    public LocalDateTime   getDateTime(){ 
    return dateTime; 
    }

    public void setDateTime(LocalDateTime dt){
    this.dateTime = dt; 
    }

    @Override
    public String toString() {
        return "Transaction{id=" + transactionId + ", type=" + type + ", amount=" + amount + "}";
    }
}
