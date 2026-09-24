package csi481.dao;

import csi481.db.DBConnection;
import csi481.model.Vaccine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VaccineDAO {

    public List<Vaccine> findAll() throws SQLException {
        String sql = "SELECT VaccineID, Name, Manufacturer, Type FROM Vaccine ORDER BY Name";
        List<Vaccine> results = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                results.add(new Vaccine(rs.getInt("VaccineID"), rs.getString("Name"),
                        rs.getString("Manufacturer"), rs.getString("Type")));
            }
        }
        return results;
    }

    public int insert(String name, String manufacturer, String type) throws SQLException {
        String sql = "INSERT INTO Vaccine (Name, Manufacturer, Type) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setString(2, manufacturer);
            pstmt.setString(3, type);
            pstmt.executeUpdate();
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Vaccine insert did not return a generated key.");
    }
}
