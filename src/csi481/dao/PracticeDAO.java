package csi481.dao;

import csi481.db.DBConnection;
import csi481.model.Practice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PracticeDAO {

    public List<Practice> findAll() throws SQLException {
        String sql = "SELECT p.PracticeID, p.Location, p.DistrictID, d.Name AS DistrictName "
                + "FROM Practice p JOIN District d ON p.DistrictID = d.DistrictID "
                + "ORDER BY p.Location";
        List<Practice> results = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Practice p = new Practice(rs.getInt("PracticeID"), rs.getString("Location"),
                        rs.getInt("DistrictID"));
                p.setDistrictName(rs.getString("DistrictName"));
                results.add(p);
            }
        }
        return results;
    }

    public int insert(String location, int districtId) throws SQLException {
        String sql = "INSERT INTO Practice (Location, DistrictID) VALUES (?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, location);
            pstmt.setInt(2, districtId);
            pstmt.executeUpdate();
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Practice insert did not return a generated key.");
    }
}
