package dao;

import model.Customer;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public int addCustomer(Customer customer) {
        String sql = "INSERT INTO Customer (Name, CNIC, Phone) VALUES (?, ?, ?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getCnic());
            ps.setString(3, customer.getPhone());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] addCustomer: " + e.getMessage());
        }
        return -1;
    }

    public Customer getCustomerById(int id) {
        String sql = "SELECT * FROM Customer WHERE Customer_ID = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] getById: " + e.getMessage());
        }
        return null;
    }

    public Customer getCustomerByCnic(String cnic) {
        String sql = "SELECT * FROM Customer WHERE CNIC = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, cnic);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] getByCnic: " + e.getMessage());
        }
        return null;
    }

    public List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM Customer ORDER BY Name";
        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] getAll: " + e.getMessage());
        }
        return list;
    }

    public boolean updateCustomer(Customer c) {
        String sql = "UPDATE Customer SET Name = ?, Phone = ? WHERE Customer_ID = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, c.getName());
            ps.setString(2, c.getPhone());
            ps.setInt(3, c.getCustomerId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] update: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteCustomer(int id) {
        String sql = "DELETE FROM Customer WHERE Customer_ID = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] delete: " + e.getMessage());
        }
        return false;
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        return new Customer(rs.getInt("Customer_ID"), rs.getString("Name"),
                rs.getString("CNIC"), rs.getString("Phone"));
    }
}
