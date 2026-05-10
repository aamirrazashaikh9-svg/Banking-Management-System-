package model;

public class Account {

    public enum AccountType { SAVINGS, CURRENT, FIXED_DEPOSIT }

    private int accountId;
    private int customerId;
    private AccountType accountType;
    private double balance;

    public Account() {}

    public Account(int accountId, int customerId, AccountType accountType, double balance) {
        this.accountId   = accountId;
        this.customerId  = customerId;
        this.accountType = accountType;
        this.balance     = balance;
    }

    public Account(int customerId, AccountType accountType) {
        this.customerId  = customerId;
        this.accountType = accountType;
        this.balance     = 0.0;
    }

    public int getAccountId(){ 
    return accountId; 
    }

    public void setAccountId(int id){ 
    this.accountId = id; 
    }

    public int getCustomerId(){ 
    return customerId; 
    }

    public void setCustomerId(int id){ 
    this.customerId = id; 
    }

    public AccountType getAccountType(){ 
    return accountType; 
    }

    public void setAccountType(AccountType type){ 
    this.accountType = type; 
    }

    public double getBalance(){ 
    return balance; 
    }

    public void setBalance(double balance){ 
    this.balance = balance; 
    }

    public void deposit(double amount){
        if (amount <= 0) throw new IllegalArgumentException("Deposit amount must be positive.");
        this.balance += amount;
    }

    public void withdraw(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Withdrawal amount must be positive.");
        if (amount > this.balance) throw new IllegalStateException("Insufficient funds.");
        this.balance -= amount;
    }

    @Override
    public String toString() {
        return "Account{id=" + accountId + ", type=" + accountType + ", balance=" + balance + "}";
    }
}
