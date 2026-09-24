package csi481.dao;

import csi481.db.DBConnection;
import csi481.model.Vaccination;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VaccinationDAO {

    /**
     * Records a vaccination that has ALREADY been given. Corresponds
     * directly to GUI task 2: register a vaccination against a vaccine,
     * patient and GP that already exist, with no re-entry of their
     * details, hence the parameters here are plain foreign key values
     * selected from drop-downs in the UI, not free-text fields.
     */
    public void insert(LocalDate dateOfVac, int boosterNo, String batchNo,
                        int birthCertNo, int gpNo, int vaccineId) throws SQLException {
        String sql = "INSERT INTO Vaccination (DateOfVac, BoosterNo, BatchNo, BirthCertNo, GPNo, VaccineID) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(dateOfVac));
            pstmt.setInt(2, boosterNo);
            pstmt.setString(3, batchNo);
            pstmt.setInt(4, birthCertNo);
            pstmt.setInt(5, gpNo);
            pstmt.setInt(6, vaccineId);
            pstmt.executeUpdate();
        }
    }

    /**
     * GUI task 4: full list of patients and their vaccination details
     * (vaccine name, booster number, date of vaccination, administering
     * GP), obtained through a direct four-way join exactly as set out in
     * the Part 1 report's process-support walkthrough for P2.
     */
    public List<Vaccination> findAllWithDetails() throws SQLException {
        String sql = "SELECT v.VaccinationID, v.DateOfVac, v.BoosterNo, v.BatchNo, "
                + "v.BirthCertNo, v.GPNo, v.VaccineID, "
                + "p.Name AS PatientName, gp.Name AS GpName, vac.Name AS VaccineName "
                + "FROM Vaccination v "
                + "JOIN Patient p ON v.BirthCertNo = p.BirthCertNo "
                + "JOIN GP gp ON v.GPNo = gp.GPNo "
                + "JOIN Vaccine vac ON v.VaccineID = vac.VaccineID "
                + "ORDER BY p.Name, v.DateOfVac";
        List<Vaccination> results = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        }
        return results;
    }

    public List<Vaccination> findByPatient(int birthCertNo) throws SQLException {
        String sql = "SELECT v.VaccinationID, v.DateOfVac, v.BoosterNo, v.BatchNo, "
                + "v.BirthCertNo, v.GPNo, v.VaccineID, "
                + "p.Name AS PatientName, gp.Name AS GpName, vac.Name AS VaccineName "
                + "FROM Vaccination v "
                + "JOIN Patient p ON v.BirthCertNo = p.BirthCertNo "
                + "JOIN GP gp ON v.GPNo = gp.GPNo "
                + "JOIN Vaccine vac ON v.VaccineID = vac.VaccineID "
                + "WHERE v.BirthCertNo = ? "
                + "ORDER BY v.DateOfVac";
        List<Vaccination> results = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, birthCertNo);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        }
        return results;
    }

    /**
     * Returns the highest BoosterNo already administered to this patient
     * for this vaccine, or 0 if none has been given yet. Used by the
     * booster-scheduling screen to work out what the NEXT booster number
     * should be. This is a plain MAX() aggregate, not a lookup against a
     * standard schedule table, because (unlike Part 1's design) this
     * diagram has no ScheduleEntry class to consult; see logical.pdf,
     * Section 5, for the consequence this has on where the interval rule
     * lives.
     */
    public int findHighestBoosterNo(int birthCertNo, int vaccineId) throws SQLException {
        String sql = "SELECT COALESCE(MAX(BoosterNo), 0) AS MaxBooster FROM Vaccination "
                + "WHERE BirthCertNo = ? AND VaccineID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, birthCertNo);
            pstmt.setInt(2, vaccineId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("MaxBooster");
                }
            }
        }
        return 0;
    }

    /**
     * Returns the date of the most recent vaccination given to this
     * patient for this vaccine, or null if none exists. Used together with
     * a hard-coded interval (see BoosterScheduler) to compute a proposed
     * next-booster date.
     */
    public LocalDate findLastVaccinationDate(int birthCertNo, int vaccineId) throws SQLException {
        String sql = "SELECT MAX(DateOfVac) AS LastDate FROM Vaccination "
                + "WHERE BirthCertNo = ? AND VaccineID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, birthCertNo);
            pstmt.setInt(2, vaccineId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getDate("LastDate") != null) {
                    return rs.getDate("LastDate").toLocalDate();
                }
            }
        }
        return null;
    }

    /**
     * Audit list, per the scenario brief: "an audit list indicating the
     * number of vaccinations by type conducted during the previous
     * six-month period." Grouped by Vaccine.Type (falling back to Name
     * when Type is not distinguishing), counting administered
     * Vaccination rows only, over the trailing six months from today.
     */
    public List<Object[]> auditCountsByType(LocalDate periodStart, LocalDate periodEnd)
            throws SQLException {
        String sql = "SELECT vac.Type, vac.Name, COUNT(*) AS Total "
                + "FROM Vaccination v JOIN Vaccine vac ON v.VaccineID = vac.VaccineID "
                + "WHERE v.DateOfVac BETWEEN ? AND ? "
                + "GROUP BY vac.Type, vac.Name "
                + "ORDER BY vac.Type, vac.Name";
        List<Object[]> results = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(periodStart));
            pstmt.setDate(2, Date.valueOf(periodEnd));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new Object[]{rs.getString("Type"), rs.getString("Name"), rs.getInt("Total")});
                }
            }
        }
        return results;
    }

    private Vaccination mapRow(ResultSet rs) throws SQLException {
        Vaccination v = new Vaccination(
                rs.getDate("DateOfVac").toLocalDate(),
                rs.getInt("BoosterNo"),
                rs.getString("BatchNo"),
                rs.getInt("BirthCertNo"),
                rs.getInt("GPNo"),
                rs.getInt("VaccineID"));
        v.setVaccinationId(rs.getInt("VaccinationID"));
        v.setPatientName(rs.getString("PatientName"));
        v.setGpName(rs.getString("GpName"));
        v.setVaccineName(rs.getString("VaccineName"));
        return v;
    }
}
