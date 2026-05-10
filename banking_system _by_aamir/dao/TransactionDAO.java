package dao;

import model.Account;
import model.Transaction;
import model.Transaction.TransactionType;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    private final AccountDAO accountDAO = new AccountDAO();

    public Transaction deposit(int toAccountId, double amount, int adminId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            Account acc = accountDAO.getAccountById(toAccountId);
            if (acc == null) throw new SQLException("Account not found: " + toAccountId);
            acc.deposit(amount);
            updateBalanceInTx(conn, toAccountId, acc.getBalance());
            Transaction tx = new Transaction(0, toAccountId, adminId, TransactionType.DEPOSIT, amount);
            int txId = insertTransaction(conn, tx);
            tx.setTransactionId(txId);
            conn.commit();
            return tx;
        } catch (Exception e) {
            rollback(conn);
            System.err.println("[TransactionDAO] deposit failed: " + e.getMessage());
            return null;
        } finally {
            resetAutoCommit(conn);
        }
    }

    public Transaction withdraw(int fromAccountId, double amount, int adminId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            Account acc = accountDAO.getAccountById(fromAccountId);
            if (acc == null) throw new SQLException("Account not found: " + fromAccountId);
            acc.withdraw(amount);
            updateBalanceInTx(conn, fromAccountId, acc.getBalance());
            Transaction tx = new Transaction(fromAccountId, 0, adminId, TransactionType.WITHDRAWAL, amount);
            int txId = insertTransaction(conn, tx);
            tx.setTransactionId(txId);
            conn.commit();
            return tx;
        } catch (Exception e) {
            rollback(conn);
            System.err.println("[TransactionDAO] withdraw failed: " + e.getMessage());
            return null;
        } finally {
            resetAutoCommit(conn);
        }
    }

    public Transaction transfer(int fromAccountId, int toAccountId, double amount, int adminId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            Account fromAcc = accountDAO.getAccountById(fromAccountId);
            Account toAcc   = accountDAO.getAccountById(toAccountId);
            if (fromAcc == null || toAcc == null) throw new SQLException("One or both accounts not found.");
            fromAcc.withdraw(amount);
            toAcc.deposit(amount);
            updateBalanceInTx(conn, fromAccountId, fromAcc.getBalance());
            updateBalanceInTx(conn, toAccountId,   toAcc.getBalance());
            Transaction tx = new Transaction(fromAccountId, toAccountId, adminId, TransactionType.TRANSFER, amount);
            int txId = insertTransaction(conn, tx);
            tx.setTransactionId(txId);
            conn.commit();
            return tx;
        } catch (Exception e) {
            rollback(conn);
            System.err.println("[TransactionDAO] transfer failed: " + e.getMessage());
            return null;
        } finally {
            resetAutoCommit(conn);
        }
    }

    public List<Transaction> getTransactionsByAccount(int accountId) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM Transactions WHERE From_Account_ID = ? OR To_Account_ID = ? ORDER BY DateTime DESC";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, accountId);
            ps.setInt(2, accountId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[TransactionDAO] getByAccount: " + e.getMessage());
        }
        return list;
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM Transactions ORDER BY DateTime DESC";
        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[TransactionDAO] getAll: " + e.getMessage());
        }
        return list;
    }

    private void updateBalanceInTx(Connection conn, int accountId, double balance) throws SQLException {
        String sql = "UPDATE Account SET Balance = ? WHERE Account_ID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, balance);
            ps.setInt(2, accountId);
            ps.executeUpdate();
        }
    }

    private int insertTransaction(Connection conn, Transaction tx) throws SQLException {
        String sql = "INSERT INTO Transactions (From_Account_ID, To_Account_ID, Admin_ID, Type, Amount, DateTime) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, tx.getFromAccountId());
            ps.setInt(2, tx.getToAccountId());
            ps.setInt(3, tx.getAdminId());
            ps.setString(4, tx.getType().name());
            ps.setDouble(5, tx.getAmount());
            ps.setTimestamp(6, Timestamp.valueOf(tx.getDateTime()));
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
            throw new SQLException("Failed to get transaction ID.");
        }
    }

    private void rollback(Connection conn) {
        try { if (conn != null) conn.rollback(); } catch (SQLException ignored) {}
    }

    private void resetAutoCommit(Connection conn) {
        try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ignored) {}
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getInt("Transaction_ID"), rs.getInt("From_Account_ID"),
                rs.getInt("To_Account_ID"),  rs.getInt("Admin_ID"),
                TransactionType.valueOf(rs.getString("Type")),
                rs.getDouble("Amount"),
                rs.getTimestamp("DateTime").toLocalDateTime());
    }
}
