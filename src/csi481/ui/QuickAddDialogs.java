package csi481.ui;

import csi481.dao.DistrictDAO;
import csi481.dao.GuardianDAO;
import csi481.dao.PracticeDAO;
import csi481.model.District;
import csi481.model.Guardian;
import csi481.model.Practice;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Small modal dialogs used to satisfy a foreign key requirement inline,
 * without forcing the user to abandon the form they are on. For example,
 * adding a GP requires an existing Practice (mandatory participation, per
 * the diagram's 1..* multiplicity), so rather than blocking the user, a
 * "+ New Practice" button opens one of these instead.
 */
final class QuickAddDialogs {

    private QuickAddDialogs() {
    }

    /** Prompts for a district name and inserts it. Returns the new District, or null if cancelled. */
    static District addDistrict(Component parent) {
        JTextField nameField = UITheme.textField();
        int result = showFormDialog(parent, "Add District", new Object[]{"District name:", nameField});
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            UITheme.showError(parent, "Add District", "District name cannot be empty.");
            return null;
        }
        try {
            int id = new DistrictDAO().insert(name);
            return new District(id, name);
        } catch (SQLException e) {
            UITheme.showError(parent, "Add District", "Could not save district:\n" + e.getMessage());
            return null;
        }
    }

    /** Prompts for a practice location and district, inserting a district first if none exist. Returns the new Practice, or null if cancelled. */
    static Practice addPractice(Component parent) {
        List<District> districts;
        try {
            districts = new DistrictDAO().findAll();
        } catch (SQLException e) {
            UITheme.showError(parent, "Add Practice", "Could not load districts:\n" + e.getMessage());
            return null;
        }
        if (districts.isEmpty()) {
            UITheme.showInfo(parent, "Add Practice",
                    "No districts exist yet. Please add a district first.");
            District d = addDistrict(parent);
            if (d == null) {
                return null;
            }
            districts.add(d);
        }

        JTextField locationField = UITheme.textField();
        JComboBox<District> districtCombo = new JComboBox<>(districts.toArray(new District[0]));
        int result = showFormDialog(parent, "Add Practice", new Object[]{
                "Practice location:", locationField,
                "District:", districtCombo
        });
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }
        String location = locationField.getText().trim();
        District selected = (District) districtCombo.getSelectedItem();
        if (location.isEmpty() || selected == null) {
            UITheme.showError(parent, "Add Practice", "Location and district are both required.");
            return null;
        }
        try {
            int id = new PracticeDAO().insert(location, selected.getDistrictId());
            Practice p = new Practice(id, location, selected.getDistrictId());
            p.setDistrictName(selected.getName());
            return p;
        } catch (SQLException e) {
            UITheme.showError(parent, "Add Practice", "Could not save practice:\n" + e.getMessage());
            return null;
        }
    }

    /** Prompts for a guardian's Omang number and name. Returns the new Guardian, or null if cancelled. */
    static Guardian addGuardian(Component parent) {
        JTextField oNoField = UITheme.textField();
        JTextField nameField = UITheme.textField();
        int result = showFormDialog(parent, "Add Parent / Guardian", new Object[]{
                "Omang number:", oNoField,
                "Full name:", nameField
        });
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }
        String name = nameField.getText().trim();
        int oNo;
        try {
            oNo = Integer.parseInt(oNoField.getText().trim());
        } catch (NumberFormatException e) {
            UITheme.showError(parent, "Add Parent / Guardian", "Omang number must be a whole number.");
            return null;
        }
        if (name.isEmpty()) {
            UITheme.showError(parent, "Add Parent / Guardian", "Name cannot be empty.");
            return null;
        }
        try {
            if (new GuardianDAO().existsByONo(oNo)) {
                UITheme.showError(parent, "Add Parent / Guardian",
                        "A guardian with Omang number " + oNo + " already exists.");
                return null;
            }
            new GuardianDAO().insert(oNo, name);
            return new Guardian(oNo, name);
        } catch (SQLException e) {
            UITheme.showError(parent, "Add Parent / Guardian", "Could not save guardian:\n" + e.getMessage());
            return null;
        }
    }

    private static int showFormDialog(Component parent, String title, Object[] labelsAndFields) {
        JPanel panel = new JPanel(new GridLayout(labelsAndFields.length / 2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        for (int i = 0; i < labelsAndFields.length; i += 2) {
            panel.add(new JLabel((String) labelsAndFields[i]));
            panel.add((Component) labelsAndFields[i + 1]);
        }
        return JOptionPane.showConfirmDialog(parent, panel, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    }
}
