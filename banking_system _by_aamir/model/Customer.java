package model;

public class Customer {
    private int    customerId;
    private String name;
    private String cnic;
    private String phone;

    public Customer() {}

    public Customer(int customerId, String name, String cnic, String phone) {
        this.customerId = customerId;
        this.name  = name;
        this.cnic  = cnic;
        this.phone = phone;
    }

    public Customer(String name, String cnic, String phone) {
        this.name  = name;
        this.cnic  = cnic;
        this.phone = phone;
    }

    public int getCustomerId(){ 
    return customerId; 
    }

    public void setCustomerId(int id){
    this.customerId = id; 
    }

    public String getName(){ 
    return name; 
    }

    public void   setName(String name){ 
    this.name = name; 
    }

    public String getCnic(){ 
    return cnic; 
    }

    public void setCnic(String cnic){ 
    this.cnic = cnic; 
    }

    public String getPhone(){
    return phone; 
    }

    public void setPhone(String phone){ 
    this.phone = phone; 
    }

    @Override
    public String toString() {
        return "Customer{id=" + customerId + ", name='" + name + "', cnic='" + cnic + "'}";
    }
}
