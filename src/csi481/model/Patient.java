package csi481.model;

import java.time.LocalDate;

/**
 * Represents an infant patient. Identified by birth certificate number
 * (birthCertNo), per R2 of the scenario brief and the diagram. Every
 * patient has exactly one guardian (oNo) and is on the list of exactly one
 * GP (gpNo) -- both foreign keys are therefore NOT NULL in the schema,
 * directly reflecting the mandatory (1) multiplicities on those two
 * associations in the diagram.
 */
public class Patient {

    private int birthCertNo;
    private String name;
    private LocalDate dateOfBirth;
    private int oNo;
    private int gpNo;
    private String guardianName; // convenience field, populated by joined queries only
    private String gpName;       // convenience field, populated by joined queries only

    public Patient() {
    }

    public Patient(int birthCertNo, String name, LocalDate dateOfBirth, int oNo, int gpNo) {
        this.birthCertNo = birthCertNo;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.oNo = oNo;
        this.gpNo = gpNo;
    }

    public int getBirthCertNo() {
        return birthCertNo;
    }

    public void setBirthCertNo(int birthCertNo) {
        this.birthCertNo = birthCertNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getoNo() {
        return oNo;
    }

    public void setoNo(int oNo) {
        this.oNo = oNo;
    }

    public int getGpNo() {
        return gpNo;
    }

    public void setGpNo(int gpNo) {
        this.gpNo = gpNo;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }

    public String getGpName() {
        return gpName;
    }

    public void setGpName(String gpName) {
        this.gpName = gpName;
    }

    @Override
    public String toString() {
        return name + " (BCN " + birthCertNo + ")";
    }
}
