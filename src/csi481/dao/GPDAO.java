package csi481.dao;

import csi481.db.DBConnection;
import csi481.model.GP;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GPDAO {

    /**
     * Returns every GP, each carrying its derived numberOfPatients count
     * (constraint C5 in the Part 1 report: /numberOfPatients is computed
     * via COUNT over Patient, never stored). The LEFT JOIN plus COUNT
     * ensures a GP with zero patients still appears, with a count of 0,
     * which matters directly for Assumption A9 (a newly appointed GP may
     * have no patients yet).
     */
    public List<GP> findAll() throws SQLException {
        String sql = "SELECT g.GPNo, g.Name, g.PracticeID, pr.Location AS PracticeLocation, "
                + "COUNT(pa.BirthCertNo) AS NumPatients "
                + "FROM GP g "
                + "JOIN Practice pr ON g.PracticeID = pr.PracticeID "
                + "LEFT JOIN Patient pa ON pa.GPNo = g.GPNo "
                + "GROUP BY g.GPNo, g.Name, g.PracticeID, pr.Location "
                + "ORDER BY g.Name";
        List<GP> results = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                GP gp = new GP(rs.getInt("GPNo"), rs.getString("Name"), rs.getInt("PracticeID"));
                gp.setPracticeLocation(rs.getString("PracticeLocation"));
                gp.setNumberOfPatients(rs.getInt("NumPatients"));
                results.add(gp);
            }
        }
        return results;
    }

    public void insert(int gpNo, String name, int practiceId) throws SQLException {
        String sql = "INSERT INTO GP (GPNo, Name, PracticeID) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, gpNo);
            pstmt.setString(2, name);
            pstmt.setInt(3, practiceId);
            pstmt.executeUpdate();
        }
    }

    public boolean existsByGpNo(int gpNo) throws SQLException {
        String sql = "SELECT 1 FROM GP WHERE GPNo = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, gpNo);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
