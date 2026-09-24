package csi481.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Application shell: a fixed left-hand navigation rail plus a CardLayout
 * content area on the right, one card per GUI task from the brief. This
 * single-window, sidebar-navigation shape was chosen over five separate
 * pop-up windows specifically so the receptionist-style user this system
 * is built for never loses track of which screen they are on, and never
 * has to hunt through overlapping windows.
 */
public class MainFrame extends JFrame {

    private static final String CARD_ADD = "ADD";
    private static final String CARD_REGISTER = "REGISTER";
    private static final String CARD_SCHEDULE = "SCHEDULE";
    private static final String CARD_LIST = "LIST";
    private static final String CARD_REPORTS = "REPORTS";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel content = new JPanel(cardLayout);
    private PatientListPanel patientListPanel;

    public MainFrame() {
        super("CSI481 -- Infant Immunisation Recording System");
        UITheme.apply();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 760);
        setMinimumSize(new Dimension(980, 620));
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(buildSidebar(), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);

        cardLayout.show(content, CARD_ADD);
    }

    private JComponent buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UITheme.PRIMARY_DARK);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBorder(new EmptyBorder(0, 0, 0, 0));

        JLabel brand = new JLabel("<html><b>CSI481</b><br/>Immunisation System</html>");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("SansSerif", Font.BOLD, 16));
        brand.setBorder(new EmptyBorder(24, 20, 24, 20));
        sidebar.add(brand);

        sidebar.add(navButton("1. Add Records", CARD_ADD));
        sidebar.add(navButton("2. Register Vaccination", CARD_REGISTER));
        sidebar.add(navButton("3. Schedule Booster", CARD_SCHEDULE));
        sidebar.add(navButton("4. Patient List", CARD_LIST));
        sidebar.add(navButton("5. Reports", CARD_REPORTS));

        sidebar.add(Box.createVerticalGlue());
        JLabel footer = new JLabel("<html><body style='width:170px'>University of Botswana &mdash; CSI481 Database Systems</body></html>");
        footer.setForeground(new Color(0xB9CBB9));
        footer.setFont(new Font("SansSerif", Font.PLAIN, 11));
        footer.setBorder(new EmptyBorder(12, 20, 20, 20));
        sidebar.add(footer);

        return sidebar;
    }

    private JButton navButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(UITheme.FONT_LABEL);
        button.setForeground(Color.WHITE);
        button.setBackground(UITheme.PRIMARY_DARK);
        button.setBorder(new EmptyBorder(12, 20, 12, 20));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> {
            cardLayout.show(content, cardName);
            if (CARD_LIST.equals(cardName) && patientListPanel != null) {
                patientListPanel.reload();
            }
        });
        button.addChangeListener(e -> {
            if (button.getModel().isRollover()) {
                button.setBackground(UITheme.PRIMARY);
            } else {
                button.setBackground(UITheme.PRIMARY_DARK);
            }
        });
        return button;
    }

    private JComponent buildContent() {
        content.setBackground(UITheme.BACKGROUND);
        content.add(new AddRecordsPanel(), CARD_ADD);
        content.add(new RegisterVaccinationPanel(), CARD_REGISTER);
        content.add(new ScheduleBoosterPanel(), CARD_SCHEDULE);
        patientListPanel = new PatientListPanel();
        content.add(patientListPanel, CARD_LIST);
        content.add(new ReportsPanel(), CARD_REPORTS);
        return content;
    }
}
