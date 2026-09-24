package csi481.ui;

import csi481.dao.VaccinationDAO;
import csi481.model.Vaccination;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * GUI task 4: "Give a list of patients and their vaccination details
 * including vaccine name, booster number (if any), date of vaccination and
 * GP details of the GP who carried out the vaccination."
 *
 * Backed directly by VaccinationDAO.findAllWithDetails(), a single joined
 * query across Vaccination, Patient, GP and Vaccine, exactly matching the
 * process-support walkthrough style used for P2 in the Part 1 report: no
 * unstated joins, one row per administered vaccination.
 */
public class PatientListPanel extends JPanel {

    private final VaccinationDAO vaccinationDAO = new VaccinationDAO();
    private final DefaultTableModel model;

    public PatientListPanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BACKGROUND);
        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setBackground(UITheme.BACKGROUND);
        titleBlock.add(UITheme.pageTitle("Patients and Their Vaccinations"));
        titleBlock.add(Box.createVerticalStrut(4));
        titleBlock.add(UITheme.pageSubtitle("One row per administered vaccination, most recent last per patient."));
        header.add(titleBlock, BorderLayout.WEST);

        JButton refreshBtn = UITheme.secondaryButton("Refresh");
        refreshBtn.addActionListener(e -> reload());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(refreshBtn);
        header.add(btnPanel, BorderLayout.EAST);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        model = new DefaultTableModel(new Object[]{
                "Patient", "Vaccine", "Booster No.", "Date Given", "Administering GP"
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

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        reload();
    }

    public void reload() {
        model.setRowCount(0);
        try {
            List<Vaccination> rows = vaccinationDAO.findAllWithDetails();
            for (Vaccination v : rows) {
                model.addRow(new Object[]{
                        v.getPatientName(),
                        v.getVaccineName(),
                        v.getBoosterNo(),
                        v.getDateOfVac(),
                        v.getGpName()
                });
            }
        } catch (SQLException e) {
            UITheme.showError(this, "Patient List", "Database error:\n" + e.getMessage());
        }
    }
}
