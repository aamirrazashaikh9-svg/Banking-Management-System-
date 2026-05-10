package dao;

import model.Admin;
import model.Admin.Role;
import util.DBConnection;

import java.sql.*;

public class AdminDAO {

    public Admin login(String username, String password) {
        String sql = "SELECT * FROM Admin WHERE Username = ? AND Password = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[AdminDAO] login: " + e.getMessage());
        }
        return null;
    }

    public int addAdmin(Admin admin) {
        String sql = "INSERT INTO Admin (Username, Password, Role) VALUES (?, ?, ?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, admin.getUsername());
            ps.setString(2, admin.getPassword());
            ps.setString(3, admin.getRole().name());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            System.err.println("[AdminDAO] add: " + e.getMessage());
        }
        return -1;
    }

    public Admin getAdminById(int id) {
        String sql = "SELECT * FROM Admin WHERE Admin_ID = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[AdminDAO] getById: " + e.getMessage());
        }
        return null;
    }

    public boolean updatePassword(int adminId, String newPassword) {
        String sql = "UPDATE Admin SET Password = ? WHERE Admin_ID = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newPassword);
            ps.setInt(2, adminId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AdminDAO] updatePassword: " + e.getMessage());
        }
        return false;
    }

    private Admin mapRow(ResultSet rs) throws SQLException {
        return new Admin(rs.getInt("Admin_ID"), rs.getString("Username"),
                rs.getString("Password"), Role.valueOf(rs.getString("Role")));
    }
}
