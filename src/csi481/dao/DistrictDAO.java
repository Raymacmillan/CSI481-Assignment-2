package csi481.dao;

import csi481.db.DBConnection;
import csi481.model.District;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access for the District table. District is not directly exposed as
 * its own "add new" screen in the application, since GUI task 1 only
 * requires adding a GP, vaccine, patient and parent, but it is read here so
 * that Practice records (added indirectly when adding a GP) can be linked
 * to a district correctly.
 */
public class DistrictDAO {

    public List<District> findAll() throws SQLException {
        String sql = "SELECT DistrictID, Name FROM District ORDER BY Name";
        List<District> results = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                results.add(new District(rs.getInt("DistrictID"), rs.getString("Name")));
            }
        }
        return results;
    }

    public int insert(String name) throws SQLException {
        String sql = "INSERT INTO District (Name) VALUES (?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("District insert did not return a generated key.");
    }
}
