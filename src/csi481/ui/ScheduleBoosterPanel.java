package csi481.ui;

import csi481.dao.AppointmentDAO;
import csi481.dao.GPDAO;
import csi481.dao.PatientDAO;
import csi481.dao.VaccinationDAO;
import csi481.dao.VaccineDAO;
import csi481.model.GP;
import csi481.model.Patient;
import csi481.model.Vaccine;
import csi481.util.BoosterScheduler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * GUI task 3: "Schedule the next booster vaccination for a patient
 * (assuming they took the first dose) and appoint with a GP for the
 * scheduled vaccination."
 *
 * The proposed booster number is one more than the highest already
 * administered (VaccinationDAO.findHighestBoosterNo). The proposed date is
 * the last administered date plus a hardcoded interval
 * (BoosterScheduler.proposeNextBoosterDate) -- see the class comment on
 * BoosterScheduler for why this interval cannot come from the database
 * itself under this diagram. Confirming here inserts a row into
 * Appointment (status SCHEDULED), not into Vaccination, since the dose has
 * not actually been given yet.
 */
public class ScheduleBoosterPanel extends JPanel {

    private final PatientDAO patientDAO = new PatientDAO();
    private final GPDAO gpDAO = new GPDAO();
    private final VaccineDAO vaccineDAO = new VaccineDAO();
    private final VaccinationDAO vaccinationDAO = new VaccinationDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    private JComboBox<Patient> patientCombo;
    private JComboBox<Vaccine> vaccineCombo;
    private JComboBox<GP> gpCombo;
    private JTextField proposedBoosterField;
    private JTextField proposedDateField;
    private JLabel statusLabel;

    public ScheduleBoosterPanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(UITheme.BACKGROUND);
        header.add(UITheme.pageTitle("Schedule Next Booster"));
        header.add(Box.createVerticalStrut(4));
        header.add(UITheme.pageSubtitle(
                "Assumes the patient has already received the previous dose. Proposes the next booster and books a GP appointment for it."));
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        JPanel card = UITheme.card();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        patientCombo = new JComboBox<>();
        vaccineCombo = new JComboBox<>();
        gpCombo = new JComboBox<>();
        proposedBoosterField = UITheme.textField();
        proposedBoosterField.setEditable(false);
        proposedDateField = UITheme.textField();
        statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_SUBTITLE);
        statusLabel.setForeground(UITheme.TEXT_MUTED);

        reloadAll();
        patientCombo.addActionListener(e -> recomputeProposal());
        vaccineCombo.addActionListener(e -> recomputeProposal());

        int row = 0;
        row = addRow(card, gbc, row, "Patient:", patientCombo);
        row = addRow(card, gbc, row, "Vaccine:", vaccineCombo);
        row = addRow(card, gbc, row, "Proposed Booster No.:", proposedBoosterField);
        row = addRow(card, gbc, row, "Proposed Date (editable, YYYY-MM-DD):", proposedDateField);
        row = addRow(card, gbc, row, "Appoint GP:", gpCombo);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        card.add(statusLabel, gbc);
        gbc.gridwidth = 1;
        row++;

        JButton refreshBtn = UITheme.secondaryButton("Refresh Lists");
        refreshBtn.addActionListener(e -> reloadAll());
        JButton scheduleBtn = UITheme.primaryButton("Confirm Appointment");
        scheduleBtn.addActionListener(e -> onSchedule());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        buttons.add(refreshBtn);
        buttons.add(scheduleBtn);
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        card.add(buttons, gbc);

        add(header, BorderLayout.NORTH);
        add(card, BorderLayout.CENTER);
    }

    private void recomputeProposal() {
        Patient patient = (Patient) patientCombo.getSelectedItem();
        Vaccine vaccine = (Vaccine) vaccineCombo.getSelectedItem();
        if (patient == null || vaccine == null) {
            statusLabel.setText(" ");
            return;
        }
        try {
            int highest = vaccinationDAO.findHighestBoosterNo(patient.getBirthCertNo(), vaccine.getVaccineId());
            if (highest == 0) {
                statusLabel.setText("Warning: no prior dose of " + vaccine.getName()
                        + " is on record for this patient. Confirm the first dose has actually been given.");
                statusLabel.setForeground(UITheme.DANGER);
            } else {
                statusLabel.setText("Last dose (booster " + highest + ") is on record. Proposing booster " + (highest + 1) + ".");
                statusLabel.setForeground(UITheme.TEXT_MUTED);
            }
            LocalDate lastDate = vaccinationDAO.findLastVaccinationDate(patient.getBirthCertNo(), vaccine.getVaccineId());
            LocalDate proposed = BoosterScheduler.proposeNextBoosterDate(lastDate, vaccine.getName());
            proposedBoosterField.setText(String.valueOf(highest + 1));
            proposedDateField.setText(proposed.toString());
        } catch (SQLException e) {
            UITheme.showError(this, "Schedule Booster", "Database error:\n" + e.getMessage());
        }
    }

    private void onSchedule() {
        Patient patient = (Patient) patientCombo.getSelectedItem();
        Vaccine vaccine = (Vaccine) vaccineCombo.getSelectedItem();
        GP gp = (GP) gpCombo.getSelectedItem();
        if (patient == null || vaccine == null || gp == null) {
            UITheme.showError(this, "Schedule Booster", "A patient, vaccine and GP must all be selected.");
            return;
        }
        int booster;
        LocalDate scheduledDate;
        try {
            booster = Integer.parseInt(proposedBoosterField.getText().trim());
        } catch (NumberFormatException ex) {
            UITheme.showError(this, "Schedule Booster", "Select a patient and vaccine to compute a booster number first.");
            return;
        }
        try {
            scheduledDate = LocalDate.parse(proposedDateField.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ex) {
            UITheme.showError(this, "Schedule Booster", "Scheduled date must be in YYYY-MM-DD format.");
            return;
        }
        try {
            appointmentDAO.insert(scheduledDate, booster, patient.getBirthCertNo(), gp.getGpNo(), vaccine.getVaccineId());
            UITheme.showInfo(this, "Schedule Booster",
                    "Booster " + booster + " of " + vaccine.getName() + " for " + patient.getName()
                            + " has been scheduled for " + scheduledDate + " with " + gp + ".");
        } catch (SQLException ex) {
            UITheme.showError(this, "Schedule Booster", "Database error:\n" + ex.getMessage());
        }
    }

    private void reloadAll() {
        reloadCombo(patientCombo, patientDAO::findAll, "Patients");
        reloadCombo(vaccineCombo, vaccineDAO::findAll, "Vaccines");
        reloadCombo(gpCombo, gpDAO::findAll, "GPs");
        recomputeProposal();
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
