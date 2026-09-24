package csi481.model;

/**
 * Represents a General Practice. Corresponds to the Practice class in the
 * model solution diagram. The diagram gives Practice only a Location
 * attribute and no identifying attribute, so a surrogate key (practiceId)
 * is introduced; see logical.pdf, Section 2.1.
 */
public class Practice {

    private int practiceId;
    private String location;
    private int districtId;
    private String districtName; // convenience field, populated by joined queries only

    public Practice() {
    }

    public Practice(int practiceId, String location, int districtId) {
        this.practiceId = practiceId;
        this.location = location;
        this.districtId = districtId;
    }

    public int getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(int practiceId) {
        this.practiceId = practiceId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    @Override
    public String toString() {
        return location + " (Practice #" + practiceId + ")";
    }
}
