package csi481.model;

/**
 * Represents the parent or guardian of an infant patient. Identified by
 * Omang number (oNo), as given directly in both the scenario brief and the
 * diagram.
 */
public class Guardian {

    private int oNo;
    private String name;

    public Guardian() {
    }

    public Guardian(int oNo, String name) {
        this.oNo = oNo;
        this.name = name;
    }

    public int getoNo() {
        return oNo;
    }

    public void setoNo(int oNo) {
        this.oNo = oNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name + " (Omang " + oNo + ")";
    }
}
