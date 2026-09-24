package csi481.dao;

import csi481.db.DBConnection;
import csi481.model.Guardian;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GuardianDAO {

    public List<Guardian> findAll() throws SQLException {
        String sql = "SELECT ONo, Name FROM Guardian ORDER BY Name";
        List<Guardian> results = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                results.add(new Guardian(rs.getInt("ONo"), rs.getString("Name")));
            }
        }
        return results;
    }

    public void insert(int oNo, String name) throws SQLException {
        String sql = "INSERT INTO Guardian (ONo, Name) VALUES (?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, oNo);
            pstmt.setString(2, name);
            pstmt.executeUpdate();
        }
    }

    public boolean existsByONo(int oNo) throws SQLException {
        String sql = "SELECT 1 FROM Guardian WHERE ONo = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, oNo);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
