package csi481.ui;

import csi481.dao.GPDAO;
import csi481.dao.PatientDAO;
import csi481.dao.VaccinationDAO;
import csi481.dao.VaccineDAO;
import csi481.model.GP;
import csi481.model.Patient;
import csi481.model.Vaccine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * GUI task 2: "Register a vaccination with a vaccine, patient and GP that
 * are already registered in the database (with no need to enter their
 * details again)."
 *
 * All three foreign keys are chosen from drop-downs sourced directly from
 * the database, so the user never re-types a patient's, GP's, or vaccine's
 * details, exactly as the brief requires. Selecting a patient and vaccine
 * auto-suggests the next booster number by querying the highest one
 * already administered for that pair, but the field stays editable in
 * case the receptionist needs to correct it.
 */
public class RegisterVaccinationPanel extends JPanel {

    private final PatientDAO patientDAO = new PatientDAO();
    private final GPDAO gpDAO = new GPDAO();
    private final VaccineDAO vaccineDAO = new VaccineDAO();
    private final VaccinationDAO vaccinationDAO = new VaccinationDAO();

    private JComboBox<Patient> patientCombo;
    private JComboBox<GP> gpCombo;
    private JComboBox<Vaccine> vaccineCombo;
    private JTextField dateField;
    private JTextField boosterField;
    private JTextField batchField;

    public RegisterVaccinationPanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(UITheme.BACKGROUND);
        header.add(UITheme.pageTitle("Register a Vaccination"));
        header.add(Box.createVerticalStrut(4));
        header.add(UITheme.pageSubtitle(
                "Record a vaccination that has already been administered, using existing records only."));
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        JPanel card = UITheme.card();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        patientCombo = new JComboBox<>();
        gpCombo = new JComboBox<>();
        vaccineCombo = new JComboBox<>();
        dateField = UITheme.textField();
        dateField.setText(LocalDate.now().toString());
        dateField.setToolTipText("Format: YYYY-MM-DD");
        boosterField = UITheme.textField();
        boosterField.setText("1");
        batchField = UITheme.textField();

        reloadAll();

        patientCombo.addActionListener(e -> suggestBoosterNumber());
        vaccineCombo.addActionListener(e -> suggestBoosterNumber());

        int row = 0;
        row = addRow(card, gbc, row, "Patient:", patientCombo);
        row = addRow(card, gbc, row, "Vaccine:", vaccineCombo);
        row = addRow(card, gbc, row, "Administering GP:", gpCombo);
        row = addRow(card, gbc, row, "Date Administered (YYYY-MM-DD):", dateField);
        row = addRow(card, gbc, row, "Booster / Dose Number:", boosterField);
        row = addRow(card, gbc, row, "Batch Number:", batchField);

        JButton refreshBtn = UITheme.secondaryButton("Refresh Lists");
        refreshBtn.addActionListener(e -> reloadAll());
        JButton saveBtn = UITheme.primaryButton("Register Vaccination");
        saveBtn.addActionListener(e -> onSave());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        buttons.add(refreshBtn);
        buttons.add(saveBtn);
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        card.add(buttons, gbc);

        add(header, BorderLayout.NORTH);
        add(card, BorderLayout.CENTER);
    }

    private void suggestBoosterNumber() {
        Patient patient = (Patient) patientCombo.getSelectedItem();
        Vaccine vaccine = (Vaccine) vaccineCombo.getSelectedItem();
        if (patient == null || vaccine == null) {
            return;
        }
        try {
            int highest = vaccinationDAO.findHighestBoosterNo(patient.getBirthCertNo(), vaccine.getVaccineId());
            boosterField.setText(String.valueOf(highest + 1));
        } catch (SQLException e) {
            // Non-fatal: leave whatever the field currently holds untouched.
        }
    }

    private void onSave() {
        Patient patient = (Patient) patientCombo.getSelectedItem();
        GP gp = (GP) gpCombo.getSelectedItem();
        Vaccine vaccine = (Vaccine) vaccineCombo.getSelectedItem();
        if (patient == null || gp == null || vaccine == null) {
            UITheme.showError(this, "Register Vaccination",
                    "A patient, vaccine and administering GP must all be selected.");
            return;
        }
        String batch = batchField.getText().trim();
        if (batch.isEmpty()) {
            UITheme.showError(this, "Register Vaccination", "Batch number cannot be empty.");
            return;
        }
        LocalDate date;
        int booster;
        try {
            date = LocalDate.parse(dateField.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ex) {
            UITheme.showError(this, "Register Vaccination", "Date must be in YYYY-MM-DD format.");
            return;
        }
        try {
            booster = Integer.parseInt(boosterField.getText().trim());
            if (booster < 1) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            UITheme.showError(this, "Register Vaccination", "Booster number must be a whole number of 1 or more.");
            return;
        }
        // Constraint C3 (temporal validity): a vaccination cannot be dated before the patient's birth.
        if (date.isBefore(patient.getDateOfBirth())) {
            UITheme.showError(this, "Register Vaccination",
                    "The vaccination date cannot be before the patient's date of birth ("
                            + patient.getDateOfBirth() + ").");
            return;
        }
        try {
            vaccinationDAO.insert(date, booster, batch, patient.getBirthCertNo(), gp.getGpNo(), vaccine.getVaccineId());
            UITheme.showInfo(this, "Register Vaccination",
                    "Vaccination recorded for " + patient.getName() + ".");
            batchField.setText("");
            suggestBoosterNumber();
        } catch (SQLException ex) {
            UITheme.showError(this, "Register Vaccination", "Database error:\n" + ex.getMessage());
        }
    }

    private void reloadAll() {
        reloadCombo(patientCombo, patientDAO::findAll, "Patients");
        reloadCombo(gpCombo, gpDAO::findAll, "GPs");
        reloadCombo(vaccineCombo, vaccineDAO::findAll, "Vaccines");
        suggestBoosterNumber();
    }

    private <T> void reloadCombo(JComboBox<T> combo, SqlSupplier<List<T>> supplier, String what) {
        combo.removeAllItems();
        try {
            for (T item : supplier.get()) {
                combo.addItem(item);
            }
        } catch (SQLException e) {
            UITheme.showError(this, "Load " + what, "Database error:\n" + e.getMessage());
        }
    }

    @FunctionalInterface
    private interface SqlSupplier<T> {
        T get() throws SQLException;
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
}
