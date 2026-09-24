package csi481.dao;

import csi481.db.DBConnection;
import csi481.model.Appointment;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access for the Appointment table, the addition beyond the model
 * solution diagram that stores a scheduled-but-not-yet-given booster
 * vaccination (see logical.pdf, Section 4, and the comment on
 * {@link Appointment} for the full justification).
 */
public class AppointmentDAO {

    public int insert(LocalDate scheduledDate, int boosterNo, int birthCertNo, int gpNo,
                       int vaccineId) throws SQLException {
        String sql = "INSERT INTO Appointment (ScheduledDate, BoosterNo, BirthCertNo, GPNo, VaccineID, Status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setDate(1, Date.valueOf(scheduledDate));
            pstmt.setInt(2, boosterNo);
            pstmt.setInt(3, birthCertNo);
            pstmt.setInt(4, gpNo);
            pstmt.setInt(5, vaccineId);
            pstmt.setString(6, Appointment.STATUS_SCHEDULED);
            pstmt.executeUpdate();
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Appointment insert did not return a generated key.");
    }

    private static final String BASE_SELECT =
            "SELECT a.AppointmentID, a.ScheduledDate, a.BoosterNo, a.BirthCertNo, a.GPNo, "
                    + "a.VaccineID, a.Status, "
                    + "p.Name AS PatientName, g.Name AS GuardianName, "
                    + "gp.Name AS GpName, pr.Location AS PracticeLocation, "
                    + "vac.Name AS VaccineName "
                    + "FROM Appointment a "
                    + "JOIN Patient p ON a.BirthCertNo = p.BirthCertNo "
                    + "JOIN Guardian g ON p.ONo = g.ONo "
                    + "JOIN GP gp ON a.GPNo = gp.GPNo "
                    + "JOIN Practice pr ON gp.PracticeID = pr.PracticeID "
                    + "JOIN Vaccine vac ON a.VaccineID = vac.VaccineID ";

    public Appointment findById(int appointmentId) throws SQLException {
        String sql = BASE_SELECT + "WHERE a.AppointmentID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, appointmentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public List<Appointment> findAllScheduled() throws SQLException {
        String sql = BASE_SELECT + "WHERE a.Status = 'SCHEDULED' ORDER BY a.ScheduledDate";
        List<Appointment> results = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        }
        return results;
    }

    /**
     * Supports the brief's "work-list by General Practice for a given
     * week": every scheduled appointment at the given practice whose date
     * falls within [weekStart, weekEnd] inclusive.
     */
    public List<Appointment> findByPracticeAndWeek(int practiceId, LocalDate weekStart,
                                                    LocalDate weekEnd) throws SQLException {
        String sql = BASE_SELECT
                + "WHERE gp.PracticeID = ? AND a.Status = 'SCHEDULED' "
                + "AND a.ScheduledDate BETWEEN ? AND ? "
                + "ORDER BY a.ScheduledDate, gp.Name";
        List<Appointment> results = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, practiceId);
            pstmt.setDate(2, Date.valueOf(weekStart));
            pstmt.setDate(3, Date.valueOf(weekEnd));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        }
        return results;
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        Appointment a = new Appointment(
                rs.getDate("ScheduledDate").toLocalDate(),
                rs.getInt("BoosterNo"),
                rs.getInt("BirthCertNo"),
                rs.getInt("GPNo"),
                rs.getInt("VaccineID"),
                rs.getString("Status"));
        a.setAppointmentId(rs.getInt("AppointmentID"));
        a.setPatientName(rs.getString("PatientName"));
        a.setGuardianName(rs.getString("GuardianName"));
        a.setGpName(rs.getString("GpName"));
        a.setPracticeLocation(rs.getString("PracticeLocation"));
        a.setVaccineName(rs.getString("VaccineName"));
        return a;
    }
}
