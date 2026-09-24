package csi481.ui;

import csi481.dao.GPDAO;
import csi481.dao.GuardianDAO;
import csi481.dao.PatientDAO;
import csi481.dao.PracticeDAO;
import csi481.dao.VaccineDAO;
import csi481.model.GP;
import csi481.model.Guardian;
import csi481.model.Practice;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * GUI task 1: "Add a new GP, vaccine, patient and parent."
 *
 * Implemented as four sub-tabs, each a self-contained form. Every
 * foreign-key field is a drop-down populated from the database, never
 * free text, so it is structurally impossible for the user to type in a
 * practice, guardian or GP that does not exist, which is exactly what the
 * NOT NULL foreign keys in the schema require to hold.
 */
public class AddRecordsPanel extends JPanel {

    private final GPDAO gpDAO = new GPDAO();
    private final PracticeDAO practiceDAO = new PracticeDAO();
    private final GuardianDAO guardianDAO = new GuardianDAO();
    private final PatientDAO patientDAO = new PatientDAO();
    private final VaccineDAO vaccineDAO = new VaccineDAO();

    public AddRecordsPanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(UITheme.BACKGROUND);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel title = UITheme.pageTitle("Add New Records");
        JLabel subtitle = UITheme.pageSubtitle(
                "Register a new GP, vaccine, patient or parent/guardian in the system.");
        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitle);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.FONT_LABEL);
        tabs.addTab("General Practitioner", buildAddGpForm());
        tabs.addTab("Vaccine", buildAddVaccineForm());
        tabs.addTab("Patient", buildAddPatientForm());
        tabs.addTab("Parent / Guardian", buildAddGuardianForm());

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    // ---------------------------------------------------------------- GP
    private JPanel buildAddGpForm() {
        JPanel card = UITheme.card();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = baseConstraints();

        JTextField gpNoField = UITheme.textField();
        JTextField nameField = UITheme.textField();
        JComboBox<Practice> practiceCombo = new JComboBox<>();
        reloadPractices(practiceCombo);
        JButton newPracticeBtn = UITheme.secondaryButton("+ New Practice");
        newPracticeBtn.addActionListener(e -> {
            Practice p = QuickAddDialogs.addPractice(this);
            if (p != null) {
                reloadPractices(practiceCombo);
                practiceCombo.setSelectedItem(p);
            }
        });

        int row = 0;
        row = addRow(card, gbc, row, "GP Number:", gpNoField);
        row = addRow(card, gbc, row, "Full Name:", nameField);
        row = addRowWithButton(card, gbc, row, "Practice:", practiceCombo, newPracticeBtn);

        JButton saveBtn = UITheme.primaryButton("Save GP");
        saveBtn.addActionListener(e -> {
            try {
                int gpNo = Integer.parseInt(gpNoField.getText().trim());
                String name = nameField.getText().trim();
                Practice practice = (Practice) practiceCombo.getSelectedItem();
                if (name.isEmpty()) {
                    UITheme.showError(this, "Add GP", "Name cannot be empty.");
                    return;
                }
                if (practice == null) {
                    UITheme.showError(this, "Add GP", "A practice must be selected.");
                    return;
                }
                if (gpDAO.existsByGpNo(gpNo)) {
                    UITheme.showError(this, "Add GP", "A GP with number " + gpNo + " already exists.");
                    return;
                }
                gpDAO.insert(gpNo, name, practice.getPracticeId());
                UITheme.showInfo(this, "Add GP", "GP " + name + " saved successfully.");
                gpNoField.setText("");
                nameField.setText("");
            } catch (NumberFormatException ex) {
                UITheme.showError(this, "Add GP", "GP number must be a whole number.");
            } catch (SQLException ex) {
                UITheme.showError(this, "Add GP", "Database error:\n" + ex.getMessage());
            }
        });
        placeSaveButton(card, gbc, row, saveBtn);
        return wrapInScroll(card);
    }

    // ------------------------------------------------------------ Vaccine
    private JPanel buildAddVaccineForm() {
        JPanel card = UITheme.card();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = baseConstraints();

        JTextField nameField = UITheme.textField();
        JTextField manufacturerField = UITheme.textField();
        JTextField typeField = UITheme.textField();

        int row = 0;
        row = addRow(card, gbc, row, "Vaccine Name:", nameField);
        row = addRow(card, gbc, row, "Manufacturer:", manufacturerField);
        row = addRow(card, gbc, row, "Type / Target Disease:", typeField);

        JButton saveBtn = UITheme.primaryButton("Save Vaccine");
        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String manufacturer = manufacturerField.getText().trim();
            String type = typeField.getText().trim();
            if (name.isEmpty()) {
                UITheme.showError(this, "Add Vaccine", "Vaccine name cannot be empty.");
                return;
            }
            try {
                vaccineDAO.insert(name, manufacturer, type);
                UITheme.showInfo(this, "Add Vaccine", "Vaccine " + name + " saved successfully.");
                nameField.setText("");
                manufacturerField.setText("");
                typeField.setText("");
            } catch (SQLException ex) {
                UITheme.showError(this, "Add Vaccine", "Database error:\n" + ex.getMessage());
            }
        });
        placeSaveButton(card, gbc, row, saveBtn);
        return wrapInScroll(card);
    }

    // ------------------------------------------------------------ Patient
    private JPanel buildAddPatientForm() {
        JPanel card = UITheme.card();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = baseConstraints();

        JTextField birthCertField = UITheme.textField();
        JTextField nameField = UITheme.textField();
        JTextField dobField = UITheme.textField();
        dobField.setToolTipText("Format: YYYY-MM-DD");
        JComboBox<Guardian> guardianCombo = new JComboBox<>();
        reloadGuardians(guardianCombo);
        JButton newGuardianBtn = UITheme.secondaryButton("+ New Guardian");
        newGuardianBtn.addActionListener(e -> {
            Guardian g = QuickAddDialogs.addGuardian(this);
            if (g != null) {
                reloadGuardians(guardianCombo);
                guardianCombo.setSelectedItem(g);
            }
        });
        JComboBox<GP> gpCombo = new JComboBox<>();
        reloadGPs(gpCombo);

        int row = 0;
        row = addRow(card, gbc, row, "Birth Certificate No.:", birthCertField);
        row = addRow(card, gbc, row, "Full Name:", nameField);
        row = addRow(card, gbc, row, "Date of Birth (YYYY-MM-DD):", dobField);
        row = addRowWithButton(card, gbc, row, "Parent / Guardian:", guardianCombo, newGuardianBtn);
        row = addRow(card, gbc, row, "Registered GP:", gpCombo);

        JButton saveBtn = UITheme.primaryButton("Save Patient");
        saveBtn.addActionListener(e -> {
            try {
                int birthCertNo = Integer.parseInt(birthCertField.getText().trim());
                String name = nameField.getText().trim();
                LocalDate dob = LocalDate.parse(dobField.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
                Guardian guardian = (Guardian) guardianCombo.getSelectedItem();
                GP gp = (GP) gpCombo.getSelectedItem();
                if (name.isEmpty()) {
                    UITheme.showError(this, "Add Patient", "Name cannot be empty.");
                    return;
                }
                if (guardian == null || gp == null) {
                    UITheme.showError(this, "Add Patient", "A guardian and a GP must both be selected.");
                    return;
                }
                if (patientDAO.existsByBirthCertNo(birthCertNo)) {
                    UITheme.showError(this, "Add Patient",
                            "A patient with birth certificate number " + birthCertNo + " already exists.");
                    return;
                }
                patientDAO.insert(birthCertNo, name, dob, guardian.getoNo(), gp.getGpNo());
                UITheme.showInfo(this, "Add Patient", "Patient " + name + " saved successfully.");
                birthCertField.setText("");
                nameField.setText("");
                dobField.setText("");
            } catch (NumberFormatException ex) {
                UITheme.showError(this, "Add Patient", "Birth certificate number must be a whole number.");
            } catch (DateTimeParseException ex) {
                UITheme.showError(this, "Add Patient", "Date of birth must be in YYYY-MM-DD format.");
            } catch (SQLException ex) {
                UITheme.showError(this, "Add Patient", "Database error:\n" + ex.getMessage());
            }
        });
        placeSaveButton(card, gbc, row, saveBtn);
        return wrapInScroll(card);
    }

    // ----------------------------------------------------------- Guardian
    private JPanel buildAddGuardianForm() {
        JPanel card = UITheme.card();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = baseConstraints();

        JTextField oNoField = UITheme.textField();
        JTextField nameField = UITheme.textField();

        int row = 0;
        row = addRow(card, gbc, row, "Omang Number:", oNoField);
        row = addRow(card, gbc, row, "Full Name:", nameField);

        JButton saveBtn = UITheme.primaryButton("Save Parent / Guardian");
        saveBtn.addActionListener(e -> {
            try {
                int oNo = Integer.parseInt(oNoField.getText().trim());
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    UITheme.showError(this, "Add Parent / Guardian", "Name cannot be empty.");
                    return;
                }
                if (guardianDAO.existsByONo(oNo)) {
                    UITheme.showError(this, "Add Parent / Guardian",
                            "A guardian with Omang number " + oNo + " already exists.");
                    return;
                }
                guardianDAO.insert(oNo, name);
                UITheme.showInfo(this, "Add Parent / Guardian", "Guardian " + name + " saved successfully.");
                oNoField.setText("");
                nameField.setText("");
            } catch (NumberFormatException ex) {
                UITheme.showError(this, "Add Parent / Guardian", "Omang number must be a whole number.");
            } catch (SQLException ex) {
                UITheme.showError(this, "Add Parent / Guardian", "Database error:\n" + ex.getMessage());
            }
        });
        placeSaveButton(card, gbc, row, saveBtn);
        return wrapInScroll(card);
    }

    // ------------------------------------------------------------- Helpers
    private void reloadPractices(JComboBox<Practice> combo) {
        combo.removeAllItems();
        try {
            List<Practice> practices = practiceDAO.findAll();
            for (Practice p : practices) {
                combo.addItem(p);
            }
        } catch (SQLException e) {
            UITheme.showError(this, "Load Practices", "Database error:\n" + e.getMessage());
        }
    }

    private void reloadGuardians(JComboBox<Guardian> combo) {
        combo.removeAllItems();
        try {
            List<Guardian> guardians = guardianDAO.findAll();
            for (Guardian g : guardians) {
                combo.addItem(g);
            }
        } catch (SQLException e) {
            UITheme.showError(this, "Load Guardians", "Database error:\n" + e.getMessage());
        }
    }

    private void reloadGPs(JComboBox<GP> combo) {
        combo.removeAllItems();
        try {
            List<GP> gps = gpDAO.findAll();
            for (GP gp : gps) {
                combo.addItem(gp);
            }
        } catch (SQLException e) {
            UITheme.showError(this, "Load GPs", "Database error:\n" + e.getMessage());
        }
    }

    private static GridBagConstraints baseConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        return gbc;
    }

    private static int addRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(UITheme.fieldLabel(label), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
        return row + 1;
    }

    private static int addRowWithButton(JPanel panel, GridBagConstraints gbc, int row, String label,
                                         JComponent field, JComponent button) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(UITheme.fieldLabel(label), gbc);

        JPanel combo = new JPanel(new BorderLayout(8, 0));
        combo.setOpaque(false);
        combo.add(field, BorderLayout.CENTER);
        combo.add(button, BorderLayout.EAST);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(combo, gbc);
        return row + 1;
    }

    private static void placeSaveButton(JPanel panel, GridBagConstraints gbc, int row, JButton button) {
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(button, gbc);
    }

    private static JPanel wrapInScroll(JPanel card) {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(UITheme.BACKGROUND);
        outer.setBorder(new EmptyBorder(16, 0, 0, 0));
        outer.add(card, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(outer);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UITheme.BACKGROUND);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return wrapPanel(scroll);
    }

    private static JPanel wrapPanel(JScrollPane scroll) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(scroll, BorderLayout.CENTER);
        wrapper.setBackground(UITheme.BACKGROUND);
        return wrapper;
    }
}
