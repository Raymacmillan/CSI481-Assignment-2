package csi481.model;

import java.time.LocalDate;

/**
 * Represents a vaccination event that has ALREADY BEEN ADMINISTERED.
 * Corresponds exactly to the Vaccination class in the model solution
 * diagram: DateOfVac, BoosterNo, BatchNo, linked to exactly one Patient,
 * one GP (the administering doctor) and one Vaccine.
 *
 * This class is deliberately kept separate from {@link Appointment}, which
 * represents a vaccination that has been SCHEDULED but not yet given. The
 * diagram itself has no notion of a future-dated booking; Appointment is
 * an addition made necessary by the application requirements (see
 * logical.pdf, Section 4, for the justification).
 */
public class Vaccination {

    private int vaccinationId;
    private LocalDate dateOfVac;
    private int boosterNo;
    private String batchNo;
    private int birthCertNo;
    private int gpNo;
    private int vaccineId;

    // Convenience fields populated only by joined report queries.
    private String patientName;
    private String gpName;
    private String vaccineName;

    public Vaccination() {
    }

    public Vaccination(LocalDate dateOfVac, int boosterNo, String batchNo,
                        int birthCertNo, int gpNo, int vaccineId) {
        this.dateOfVac = dateOfVac;
        this.boosterNo = boosterNo;
        this.batchNo = batchNo;
        this.birthCertNo = birthCertNo;
        this.gpNo = gpNo;
        this.vaccineId = vaccineId;
    }

    public int getVaccinationId() {
        return vaccinationId;
    }

    public void setVaccinationId(int vaccinationId) {
        this.vaccinationId = vaccinationId;
    }

    public LocalDate getDateOfVac() {
        return dateOfVac;
    }

    public void setDateOfVac(LocalDate dateOfVac) {
        this.dateOfVac = dateOfVac;
    }

    public int getBoosterNo() {
        return boosterNo;
    }

    public void setBoosterNo(int boosterNo) {
        this.boosterNo = boosterNo;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
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
}
