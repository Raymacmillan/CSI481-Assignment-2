package csi481.dao;

import csi481.db.DBConnection;
import csi481.model.Patient;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    public List<Patient> findAll() throws SQLException {
        String sql = "SELECT p.BirthCertNo, p.Name, p.DateOfBirth, p.ONo, p.GPNo, "
                + "g.Name AS GuardianName, gp.Name AS GpName "
                + "FROM Patient p "
                + "JOIN Guardian g ON p.ONo = g.ONo "
                + "JOIN GP gp ON p.GPNo = gp.GPNo "
                + "ORDER BY p.Name";
        List<Patient> results = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Patient patient = mapRow(rs);
                results.add(patient);
            }
        }
        return results;
    }

    private Patient mapRow(ResultSet rs) throws SQLException {
        Patient patient = new Patient(
                rs.getInt("BirthCertNo"),
                rs.getString("Name"),
                rs.getDate("DateOfBirth").toLocalDate(),
                rs.getInt("ONo"),
                rs.getInt("GPNo"));
        patient.setGuardianName(rs.getString("GuardianName"));
        patient.setGpName(rs.getString("GpName"));
        return patient;
    }

    public void insert(int birthCertNo, String name, LocalDate dateOfBirth, int oNo, int gpNo)
            throws SQLException {
        String sql = "INSERT INTO Patient (BirthCertNo, Name, DateOfBirth, ONo, GPNo) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, birthCertNo);
            pstmt.setString(2, name);
            pstmt.setDate(3, Date.valueOf(dateOfBirth));
            pstmt.setInt(4, oNo);
            pstmt.setInt(5, gpNo);
            pstmt.executeUpdate();
        }
    }

    public boolean existsByBirthCertNo(int birthCertNo) throws SQLException {
        String sql = "SELECT 1 FROM Patient WHERE BirthCertNo = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, birthCertNo);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
