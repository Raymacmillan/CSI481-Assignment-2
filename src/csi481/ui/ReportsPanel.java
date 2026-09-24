package csi481.ui;

import csi481.dao.AppointmentDAO;
import csi481.dao.PracticeDAO;
import csi481.dao.VaccinationDAO;
import csi481.model.Appointment;
import csi481.model.Practice;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/**
 * GUI task 5: "Produce reports as stated at the end of the scenario", i.e.
 * the three processes named in the brief: appointment letters, a weekly
 * work-list per practice, and a six-month audit list by vaccination type.
 * Implemented as three sub-tabs.
 */
public class ReportsPanel extends JPanel {

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final PracticeDAO practiceDAO = new PracticeDAO();
    private final VaccinationDAO vaccinationDAO = new VaccinationDAO();

    public ReportsPanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(UITheme.BACKGROUND);
        header.add(UITheme.pageTitle("Reports"));
        header.add(Box.createVerticalStrut(4));
        header.add(UITheme.pageSubtitle("Appointment letters, weekly practice work-lists, and the six-month vaccination audit."));
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.FONT_LABEL);
        tabs.addTab("Appointment Letters", buildAppointmentLetterTab());
        tabs.addTab("Weekly Work-List", buildWorkListTab());
        tabs.addTab("Six-Month Audit", buildAuditTab());

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    // ------------------------------------------------- Appointment letters
    private JPanel buildAppointmentLetterTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BACKGROUND);
        panel.setBorder(new EmptyBorder(16, 0, 0, 0));

        JComboBox<Appointment> appointmentCombo = new JComboBox<>();
        reloadAppointments(appointmentCombo);

        JTextArea letterArea = new JTextArea();
        letterArea.setFont(UITheme.FONT_MONO);
        letterArea.setEditable(false);
        letterArea.setLineWrap(true);
        letterArea.setWrapStyleWord(true);
        letterArea.setBorder(new EmptyBorder(16, 16, 16, 16));
        JScrollPane letterScroll = new JScrollPane(letterArea);
        letterScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));

        JButton generateBtn = UITheme.primaryButton("Generate Letter");
        generateBtn.addActionListener(e -> {
            Appointment selected = (Appointment) appointmentCombo.getSelectedItem();
            if (selected == null) {
                UITheme.showError(this, "Appointment Letter", "There are no scheduled appointments to write a letter for.");
                return;
            }
            letterArea.setText(buildLetterText(selected));
            letterArea.setCaretPosition(0);
        });
        JButton refreshBtn = UITheme.secondaryButton("Refresh List");
        refreshBtn.addActionListener(e -> reloadAppointments(appointmentCombo));
        JButton saveBtn = UITheme.secondaryButton("Save Letter As...");
        saveBtn.addActionListener(e -> {
            if (letterArea.getText().isBlank()) {
                UITheme.showError(this, "Save Letter", "Generate a letter first.");
                return;
            }
            saveTextToFile(letterArea.getText(), "appointment_letter.txt");
        });

        JPanel controls = new JPanel(new BorderLayout(10, 0));
        controls.setOpaque(false);
        controls.add(UITheme.fieldLabel("Scheduled Appointment:"), BorderLayout.WEST);
        controls.add(appointmentCombo, BorderLayout.CENTER);
        JPanel controlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controlButtons.setOpaque(false);
        controlButtons.add(refreshBtn);
        controlButtons.add(generateBtn);
        controls.add(controlButtons, BorderLayout.EAST);

        JPanel bottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomButtons.setOpaque(false);
        bottomButtons.add(saveBtn);

        panel.add(controls, BorderLayout.NORTH);
        panel.add(letterScroll, BorderLayout.CENTER);
        panel.add(bottomButtons, BorderLayout.SOUTH);
        return panel;
    }

    private void reloadAppointments(JComboBox<Appointment> combo) {
        combo.removeAllItems();
        try {
            List<Appointment> appointments = appointmentDAO.findAllScheduled();
            for (Appointment a : appointments) {
                combo.addItem(a);
            }
        } catch (SQLException e) {
            UITheme.showError(this, "Load Appointments", "Database error:\n" + e.getMessage());
        }
    }

    private String buildLetterText(Appointment a) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy");
        StringBuilder sb = new StringBuilder();
        sb.append("IMMUNISATION APPOINTMENT LETTER\n");
        sb.append("================================\n\n");
        sb.append("Practice: ").append(a.getPracticeLocation()).append("\n");
        sb.append("Date issued: ").append(LocalDate.now().format(fmt)).append("\n\n");
        sb.append("Dear ").append(a.getGuardianName()).append(",\n\n");
        sb.append("This letter is to confirm an immunisation appointment for your child, ")
                .append(a.getPatientName()).append(".\n\n");
        sb.append("Vaccine:          ").append(a.getVaccineName()).append("\n");
        sb.append("Booster / Dose #: ").append(a.getBoosterNo()).append("\n");
        sb.append("Appointment date: ").append(a.getScheduledDate().format(fmt)).append("\n");
        sb.append("Attending GP:     Dr. ").append(a.getGpName()).append("\n\n");
        sb.append("Please attend the above practice on the date shown, bringing your child's ")
                .append("immunisation card if you have one. If this date is not convenient, please ")
                .append("contact the practice to reschedule.\n\n");
        sb.append("Regards,\n");
        sb.append(a.getPracticeLocation()).append(" Immunisation Team\n");
        return sb.toString();
    }

    // ---------------------------------------------------------- Work-list
    private JPanel buildWorkListTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BACKGROUND);
        panel.setBorder(new EmptyBorder(16, 0, 0, 0));

        JComboBox<Practice> practiceCombo = new JComboBox<>();
        reloadPractices(practiceCombo);
        JTextField weekStartField = UITheme.textField();
        weekStartField.setText(LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toString());
        weekStartField.setToolTipText("Any date in the target week (YYYY-MM-DD); the Monday of that week is used automatically.");

        DefaultTableModel model = new DefaultTableModel(new Object[]{
                "Date", "Patient", "Vaccine", "Booster No.", "GP"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        UITheme.styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));

        JButton generateBtn = UITheme.primaryButton("Generate Work-List");
        generateBtn.addActionListener(e -> {
            Practice practice = (Practice) practiceCombo.getSelectedItem();
            if (practice == null) {
                UITheme.showError(this, "Work-List", "No practices exist yet.");
                return;
            }
            LocalDate anyDayInWeek;
            try {
                anyDayInWeek = LocalDate.parse(weekStartField.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException ex) {
                UITheme.showError(this, "Work-List", "Date must be in YYYY-MM-DD format.");
                return;
            }
            LocalDate monday = anyDayInWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate sunday = monday.plusDays(6);
            model.setRowCount(0);
            try {
                List<Appointment> appointments = appointmentDAO.findByPracticeAndWeek(
                        practice.getPracticeId(), monday, sunday);
                for (Appointment a : appointments) {
                    model.addRow(new Object[]{
                            a.getScheduledDate(), a.getPatientName(), a.getVaccineName(),
                            a.getBoosterNo(), a.getGpName()
                    });
                }
                if (appointments.isEmpty()) {
                    UITheme.showInfo(this, "Work-List",
                            "No scheduled appointments at " + practice.getLocation()
                                    + " for the week of " + monday + " to " + sunday + ".");
                }
            } catch (SQLException ex) {
                UITheme.showError(this, "Work-List", "Database error:\n" + ex.getMessage());
            }
        });
        JButton refreshBtn = UITheme.secondaryButton("Refresh Practices");
        refreshBtn.addActionListener(e -> reloadPractices(practiceCombo));

        JPanel controls = new JPanel(new GridBagLayout());
        controls.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        controls.add(UITheme.fieldLabel("Practice:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        controls.add(practiceCombo, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        controls.add(refreshBtn, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        controls.add(UITheme.fieldLabel("Any date in target week:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        controls.add(weekStartField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        controls.add(generateBtn, gbc);

        panel.add(controls, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

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

    // --------------------------------------------------------- Audit list
    private JPanel buildAuditTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BACKGROUND);
        panel.setBorder(new EmptyBorder(16, 0, 0, 0));

        JLabel periodLabel = UITheme.pageSubtitle(" ");
        DefaultTableModel model = new DefaultTableModel(new Object[]{
                "Vaccine Type", "Vaccine Name", "Number of Vaccinations"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        UITheme.styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));

        JButton generateBtn = UITheme.primaryButton("Generate Six-Month Audit");
        generateBtn.addActionListener(e -> {
            LocalDate end = LocalDate.now();
            LocalDate start = end.minusMonths(6);
            periodLabel.setText("Period covered: " + start + " to " + end);
            model.setRowCount(0);
            try {
                List<Object[]> rows = vaccinationDAO.auditCountsByType(start, end);
                int total = 0;
                for (Object[] r : rows) {
                    model.addRow(r);
                    total += (Integer) r[2];
                }
                if (rows.isEmpty()) {
                    UITheme.showInfo(this, "Six-Month Audit",
                            "No vaccinations were recorded in the period " + start + " to " + end + ".");
                } else {
                    model.addRow(new Object[]{"TOTAL", "", total});
                }
            } catch (SQLException ex) {
                UITheme.showError(this, "Six-Month Audit", "Database error:\n" + ex.getMessage());
            }
        });

        JPanel controls = new JPanel(new BorderLayout());
        controls.setOpaque(false);
        controls.add(periodLabel, BorderLayout.WEST);
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnWrap.setOpaque(false);
        btnWrap.add(generateBtn);
        controls.add(btnWrap, BorderLayout.EAST);

        panel.add(controls, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void saveTextToFile(String content, String suggestedName) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File(suggestedName));
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try (Writer writer = new FileWriter(chooser.getSelectedFile())) {
            writer.write(content);
            UITheme.showInfo(this, "Save Letter", "Letter saved to " + chooser.getSelectedFile().getAbsolutePath());
        } catch (IOException e) {
            UITheme.showError(this, "Save Letter", "Could not save file:\n" + e.getMessage());
        }
    }
}
