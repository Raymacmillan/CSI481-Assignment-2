package csi481.model;

/**
 * Represents a health District, the top-level organisational unit in the
 * region. Corresponds to the District class in the model solution UML
 * diagram. The diagram marks no identifying attribute for this class, so a
 * surrogate key (districtId) is introduced; see logical.pdf, Section 2.1.
 */
public class District {

    private int districtId;
    private String name;

    public District() {
    }

    public District(int districtId, String name) {
        this.districtId = districtId;
        this.name = name;
    }

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        // Used directly by JComboBox rendering throughout the UI layer.
        return name;
    }
}
