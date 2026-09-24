package csi481.model;

/**
 * Represents a General Practitioner. GPNo is the natural identifier given
 * directly in the scenario brief and the diagram (GPs are identified by
 * unique GP numbers), so no surrogate key is required here.
 *
 * numberOfPatients is a DERIVED attribute (marked with a leading slash in
 * the conceptual model). It is never stored on this class as a column; it
 * is computed on demand by GPDAO via COUNT(*) over Patient, and only ever
 * populated onto instances of this class when a query specifically asks
 * for it, exactly as constraint C5 in the Part 1 report requires.
 */
public class GP {

    private int gpNo;
    private String name;
    private int practiceId;
    private String practiceLocation; // convenience field, populated by joined queries only
    private Integer numberOfPatients; // null unless explicitly computed and attached

    public GP() {
    }

    public GP(int gpNo, String name, int practiceId) {
        this.gpNo = gpNo;
        this.name = name;
        this.practiceId = practiceId;
    }

    public int getGpNo() {
        return gpNo;
    }

    public void setGpNo(int gpNo) {
        this.gpNo = gpNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(int practiceId) {
        this.practiceId = practiceId;
    }

    public String getPracticeLocation() {
        return practiceLocation;
    }

    public void setPracticeLocation(String practiceLocation) {
        this.practiceLocation = practiceLocation;
    }

    public Integer getNumberOfPatients() {
        return numberOfPatients;
    }

    public void setNumberOfPatients(Integer numberOfPatients) {
        this.numberOfPatients = numberOfPatients;
    }

    @Override
    public String toString() {
        return "Dr. " + name + " (GP #" + gpNo + ")";
    }
}
