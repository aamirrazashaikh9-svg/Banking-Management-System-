package dao;

import model.Account;
import model.Account.AccountType;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    public int addAccount(Account account) {
        String sql = "INSERT INTO Account (Customer_ID, Account_Type, Balance) VALUES (?, ?, ?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, account.getCustomerId());
            ps.setString(2, account.getAccountType().name());
            ps.setDouble(3, account.getBalance());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            System.err.println("[AccountDAO] add: " + e.getMessage());
        }
        return -1;
    }

    public Account getAccountById(int id) {
        String sql = "SELECT * FROM Account WHERE Account_ID = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[AccountDAO] getById: " + e.getMessage());
        }
        return null;
    }

    public List<Account> getAccountsByCustomer(int customerId) {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM Account WHERE Customer_ID = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[AccountDAO] getByCustomer: " + e.getMessage());
        }
        return list;
    }

    public List<Account> getAllAccounts() {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM Account";
        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[AccountDAO] getAll: " + e.getMessage());
        }
        return list;
    }

    public boolean updateBalance(int accountId, double newBalance) {
        String sql = "UPDATE Account SET Balance = ? WHERE Account_ID = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, newBalance);
            ps.setInt(2, accountId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AccountDAO] updateBalance: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteAccount(int accountId) {
        String sql = "DELETE FROM Account WHERE Account_ID = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, accountId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AccountDAO] delete: " + e.getMessage());
        }
        return false;
    }

    private Account mapRow(ResultSet rs) throws SQLException {
        return new Account(rs.getInt("Account_ID"), rs.getInt("Customer_ID"),
                AccountType.valueOf(rs.getString("Account_Type")), rs.getDouble("Balance"));
    }
}
