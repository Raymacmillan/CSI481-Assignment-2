package csi481.model;

import java.time.LocalDate;

/**
 * Represents a SCHEDULED, not-yet-administered booster vaccination, with a
 * GP appointed to give it. This class has no counterpart in the model
 * solution diagram: the diagram's Vaccination class only carries
 * DateOfVac, i.e. a date already in the past for a dose already given, and
 * has no mechanism for representing a future booking.
 *
 * It was added because GUI task 3 explicitly requires "schedule the next
 * booster vaccination... and appoint with a GP for the scheduled
 * vaccination", and because the work-list and appointment-letter reports
 * both operate over future, not-yet-given appointments. See logical.pdf,
 * Section 4, for the full justification of this deliberate deviation.
 *
 * Status transitions: SCHEDULED -> COMPLETED (once the vaccination is
 * actually given, at which point a corresponding row should be inserted
 * into Vaccination) or SCHEDULED -> CANCELLED.
 */
public class Appointment {

    public static final String STATUS_SCHEDULED = "SCHEDULED";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    private int appointmentId;
    private LocalDate scheduledDate;
    private int boosterNo;
    private int birthCertNo;
    private int gpNo;
    private int vaccineId;
    private String status;

    // Convenience fields populated only by joined report queries.
    private String patientName;
    private String gpName;
    private String vaccineName;
    private String practiceLocation;
    private String guardianName;

    public Appointment() {
    }

    public Appointment(LocalDate scheduledDate, int boosterNo, int birthCertNo,
                        int gpNo, int vaccineId, String status) {
        this.scheduledDate = scheduledDate;
        this.boosterNo = boosterNo;
        this.birthCertNo = birthCertNo;
        this.gpNo = gpNo;
        this.vaccineId = vaccineId;
        this.status = status;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public int getBoosterNo() {
        return boosterNo;
    }

    public void setBoosterNo(int boosterNo) {
        this.boosterNo = boosterNo;
    }

    public int getBirthCertNo() {
        return birthCertNo;
    }

    public void setBirthCertNo(int birthCertNo) {
        this.birthCertNo = birthCertNo;
    }

    public int getGpNo() {
        return gpNo;
    }

    public void setGpNo(int gpNo) {
        this.gpNo = gpNo;
    }

    public int getVaccineId() {
        return vaccineId;
    }

    public void setVaccineId(int vaccineId) {
        this.vaccineId = vaccineId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getGpName() {
        return gpName;
    }

    public void setGpName(String gpName) {
        this.gpName = gpName;
    }

    public String getVaccineName() {
        return vaccineName;
    }

    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
    }

    public String getPracticeLocation() {
        return practiceLocation;
    }

    public void setPracticeLocation(String practiceLocation) {
        this.practiceLocation = practiceLocation;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }
}
