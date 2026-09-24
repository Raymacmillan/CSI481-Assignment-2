package csi481.model;

/**
 * Represents a vaccine type (e.g. BCG, DTP, Measles-Rubella). The diagram
 * marks no identifying attribute for this class, so a surrogate key
 * (vaccineId) is introduced; see logical.pdf, Section 2.1.
 */
public class Vaccine {

    private int vaccineId;
    private String name;
    private String manufacturer;
    private String type;

    public Vaccine() {
    }

    public Vaccine(int vaccineId, String name, String manufacturer, String type) {
        this.vaccineId = vaccineId;
        this.name = name;
        this.manufacturer = manufacturer;
        this.type = type;
    }

    public int getVaccineId() {
        return vaccineId;
    }

    public void setVaccineId(int vaccineId) {
        this.vaccineId = vaccineId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return name + " (" + type + ")";
    }
}
