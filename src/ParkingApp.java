import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Desktop UI for the EZPark Parking Management System. */
public class ParkingApp extends JFrame {
    private final ParkingLot lot = new ParkingLot();
    private final JLabel twoWheelerCount = new JLabel();
    private final JLabel fourWheelerCount = new JLabel();
    private final JPanel twoWheelerSlots = new JPanel(new GridLayout(2, 5, 8, 8));
    private final JPanel fourWheelerSlots = new JPanel(new GridLayout(1, 5, 8, 8));
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Ticket", "Vehicle", "Owner", "Type", "Slot", "Entry time"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable activeTable = new JTable(tableModel);
    private final JLabel totalRevenue = new JLabel();
    private final JLabel todayRevenue = new JLabel();
    private final JLabel monthRevenue = new JLabel();
    private final JLabel completedTransactions = new JLabel();
    private final JLabel monthlyPassCount = new JLabel();
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    public ParkingApp() {
        setTitle("EZPark | Parking Management");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 620));
        setLocationByPlatform(true);

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(new Color(245, 247, 250));
        root.setBorder(new EmptyBorder(22, 28, 28, 28));
        setContentPane(root);
        root.add(header(), BorderLayout.NORTH);
        root.add(content(), BorderLayout.CENTER);
        refreshDashboard();
        pack();
    }

    private JComponent header() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel title = new JLabel("EZPark");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(20, 60, 110));
        JLabel subtitle = new JLabel("Parking Slot Management System");
        subtitle.setForeground(new Color(85, 95, 110));
        JPanel text = new JPanel(new GridLayout(2, 1));
        text.setOpaque(false);
        text.add(title); text.add(subtitle);
        panel.add(text, BorderLayout.WEST);
        return panel;
    }

    private JComponent content() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 14));
        tabs.addTab("Dashboard", dashboardPanel());
        tabs.addTab("Park Vehicle", parkPanel());
        tabs.addTab("Checkout", checkoutPanel());
        tabs.addTab("Active Vehicles", activeVehiclesPanel());
        tabs.addTab("Monthly Passes", monthlyPassPanel());
        tabs.addTab("Admin Reports", adminReportsPanel());
        return tabs;
    }

    private JComponent dashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBorder(new EmptyBorder(22, 12, 12, 12));
        panel.setOpaque(false);
        JPanel cards = new JPanel(new GridLayout(1, 2, 18, 0));
        cards.setOpaque(false);
        cards.add(statCard("Two-wheeler slots available", twoWheelerCount, new Color(28, 132, 102)));
        cards.add(statCard("Four-wheeler slots available", fourWheelerCount, new Color(53, 102, 180)));
        panel.add(cards, BorderLayout.NORTH);

        JPanel slots = new JPanel();
        slots.setOpaque(false);
        slots.setLayout(new BoxLayout(slots, BoxLayout.Y_AXIS));
        slots.add(slotSection("Two-wheeler parking", twoWheelerSlots));
        slots.add(Box.createVerticalStrut(24));
        slots.add(slotSection("Four-wheeler parking", fourWheelerSlots));
        panel.add(slots, BorderLayout.CENTER);
        return new JScrollPane(panel);
    }

    private JComponent statCard(String heading, JLabel count, Color color) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel label = new JLabel(heading);
        label.setForeground(new Color(85, 95, 110));
        count.setFont(new Font("SansSerif", Font.BOLD, 32));
        count.setForeground(color);
        card.add(label, BorderLayout.NORTH); card.add(count, BorderLayout.CENTER);
        return card;
    }

    private JComponent slotSection(String title, JPanel grid) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        JLabel label = new JLabel(title + "     Green = available     Red = occupied");
        label.setFont(new Font("SansSerif", Font.BOLD, 15));
        panel.add(label, BorderLayout.NORTH); panel.add(grid, BorderLayout.CENTER);
        return panel;
    }

    private JComponent parkPanel() {
        JTextField ownerField = new JTextField();
        JTextField vehicleField = new JTextField();
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Two-wheeler", "Four-wheeler"});
        JButton parkButton = new JButton("Allocate Slot & Create Ticket");
        parkButton.addActionListener(e -> {
            String owner = ownerField.getText().trim();
            String number = vehicleField.getText().trim();
            if (owner.isEmpty() || number.isEmpty()) {
                showMessage("Enter both the owner's name and vehicle number.", "Missing details", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Vehicle vehicle = typeBox.getSelectedIndex() == 0
                    ? new TwoWheeler(number, owner) : new FourWheeler(number, owner);
            Ticket ticket = lot.parkVehicleAndGetTicket(vehicle);
            if (ticket == null) {
                showMessage("This vehicle is already parked or that zone is full.", "Parking unavailable", JOptionPane.ERROR_MESSAGE);
                return;
            }
            ownerField.setText(""); vehicleField.setText("");
            refreshDashboard();
            showMessage("Vehicle parked successfully.\n\nTicket: " + ticket.getTicketId()
                    + "\nSlot: " + ticket.getSlotNumber() + "\nEntry: " + ticket.getEntryTime().format(TIME_FORMAT),
                    "Parking ticket", JOptionPane.INFORMATION_MESSAGE);
        });
        return formPanel("Park a vehicle", "Vehicle details", new String[]{"Owner name", "Vehicle number", "Vehicle type"},
                new JComponent[]{ownerField, vehicleField, typeBox}, parkButton);
    }

    private JComponent checkoutPanel() {
        JTextField vehicleField = new JTextField();
        JButton checkoutButton = new JButton("Checkout & Calculate Bill");
        checkoutButton.addActionListener(e -> {
            String number = vehicleField.getText().trim();
            if (number.isEmpty()) {
                showMessage("Enter the vehicle number.", "Missing vehicle number", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Ticket ticket = lot.exitVehicleAndGetTicket(number);
            if (ticket == null) {
                showMessage("No active parking record was found for " + number.toUpperCase() + ".", "Not found", JOptionPane.ERROR_MESSAGE);
                return;
            }
            vehicleField.setText(""); refreshDashboard();
            String passNote = ticket.isMonthlyPass() ? "\nMonthly pass applied" : "";
            showMessage("Checkout complete.\n\nTicket: " + ticket.getTicketId()
                    + "\nVehicle: " + ticket.getVehicle().getVehicleNumber()
                    + "\nTotal charge: Rs. " + String.format("%.2f", ticket.calculateCharge()) + passNote,
                    "Parking bill", JOptionPane.INFORMATION_MESSAGE);
        });
        return formPanel("Checkout a vehicle", "Enter the vehicle number to calculate the parking charge and free its slot.",
                new String[]{"Vehicle number"}, new JComponent[]{vehicleField}, checkoutButton);
    }

    private JComponent formPanel(String title, String note, String[] labels, JComponent[] fields, JButton action) {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(28, 34, 28, 34));
        form.setPreferredSize(new Dimension(560, 350));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2; c.anchor = GridBagConstraints.WEST;
        JLabel heading = new JLabel(title); heading.setFont(new Font("SansSerif", Font.BOLD, 22)); form.add(heading, c);
        c.gridy++; c.insets = new Insets(5, 0, 22, 0);
        JLabel description = new JLabel(note); description.setForeground(new Color(85, 95, 110)); form.add(description, c);
        for (int i = 0; i < labels.length; i++) {
            c.gridy++; c.gridwidth = 1; c.insets = new Insets(8, 0, 8, 16); c.weightx = 0; c.fill = GridBagConstraints.NONE;
            form.add(new JLabel(labels[i]), c);
            c.gridx = 1; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL; c.insets = new Insets(8, 0, 8, 0);
            fields[i].setPreferredSize(new Dimension(290, 32)); form.add(fields[i], c); c.gridx = 0;
        }
        c.gridy++; c.gridwidth = 2; c.insets = new Insets(22, 0, 0, 0); c.fill = GridBagConstraints.NONE;
        action.setBackground(new Color(20, 86, 155)); action.setForeground(Color.WHITE); action.setFocusPainted(false);
        form.add(action, c); outer.add(form); return outer;
    }

    private JComponent activeVehiclesPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBorder(new EmptyBorder(20, 12, 12, 12)); panel.setOpaque(false);
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        controls.setOpaque(false);
        JTextField ownerSearch = new JTextField(18);
        JButton search = new JButton("Search owner");
        JButton clear = new JButton("Show all");
        search.addActionListener(e -> populateTickets(lot.searchActiveByOwner(ownerSearch.getText())));
        clear.addActionListener(e -> { ownerSearch.setText(""); populateTickets(lot.getActiveTickets()); });
        controls.add(new JLabel("Owner name:")); controls.add(ownerSearch); controls.add(search); controls.add(clear);
        panel.add(controls, BorderLayout.NORTH);
        activeTable.setRowHeight(28); activeTable.setFillsViewportHeight(true);
        panel.add(new JScrollPane(activeTable), BorderLayout.CENTER); return panel;
    }

    private JComponent monthlyPassPanel() {
        JTextField ownerField = new JTextField();
        JTextField vehicleField = new JTextField();
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Two-wheeler", "Four-wheeler"});
        JTextField expiryField = new JTextField(LocalDate.now().plusMonths(1).toString());
        JButton register = new JButton("Save Monthly Pass");
        register.addActionListener(e -> {
            String owner = ownerField.getText().trim();
            String vehicle = vehicleField.getText().trim();
            if (owner.isEmpty() || vehicle.isEmpty()) {
                showMessage("Enter an owner name and vehicle number.", "Missing details", JOptionPane.WARNING_MESSAGE); return;
            }
            try {
                LocalDate expiry = LocalDate.parse(expiryField.getText().trim());
                if (!expiry.isAfter(LocalDate.now())) throw new IllegalArgumentException();
                String type = typeBox.getSelectedIndex() == 0 ? "TWO_WHEELER" : "FOUR_WHEELER";
                lot.registerMonthlyPass(owner, vehicle, type, expiry);
                ownerField.setText(""); vehicleField.setText(""); refreshDashboard();
                showMessage("Monthly pass saved. Parking charges will be Rs. 0 until " + expiry + ".", "Pass registered", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                showMessage("Use a future date in YYYY-MM-DD format.", "Invalid expiry date", JOptionPane.WARNING_MESSAGE);
            }
        });
        return formPanel("Monthly pass", "Register a regular vehicle for free parking until its expiry date.",
                new String[]{"Owner name", "Vehicle number", "Vehicle type", "Expires on (YYYY-MM-DD)"},
                new JComponent[]{ownerField, vehicleField, typeBox, expiryField}, register);
    }

    private JComponent adminReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBorder(new EmptyBorder(22, 12, 12, 12)); panel.setOpaque(false);
        JPanel cards = new JPanel(new GridLayout(2, 3, 18, 18)); cards.setOpaque(false);
        cards.add(statCard("Total revenue", totalRevenue, new Color(20, 86, 155)));
        cards.add(statCard("Today's revenue", todayRevenue, new Color(28, 132, 102)));
        cards.add(statCard("This month's revenue", monthRevenue, new Color(124, 75, 170)));
        cards.add(statCard("Completed transactions", completedTransactions, new Color(175, 105, 25)));
        cards.add(statCard("Active monthly passes", monthlyPassCount, new Color(39, 112, 135)));
        JPanel blank = new JPanel(); blank.setOpaque(false); cards.add(blank);
        panel.add(cards, BorderLayout.NORTH);
        JButton refresh = new JButton("Refresh report"); refresh.addActionListener(e -> refreshDashboard());
        panel.add(refresh, BorderLayout.CENTER); return panel;
    }

    private void refreshDashboard() {
        twoWheelerCount.setText(lot.getAvailableTwoWheelerSlots() + " of 10");
        fourWheelerCount.setText(lot.getAvailableFourWheelerSlots() + " of 5");
        renderSlots(twoWheelerSlots, lot.getTwoWheelerSlots(), "TW");
        renderSlots(fourWheelerSlots, lot.getFourWheelerSlots(), "FW");
        populateTickets(lot.getActiveTickets());
        RevenueReport report = lot.getRevenueReport();
        totalRevenue.setText("Rs. " + String.format("%.2f", report.totalRevenue()));
        todayRevenue.setText("Rs. " + String.format("%.2f", report.todayRevenue()));
        monthRevenue.setText("Rs. " + String.format("%.2f", report.monthRevenue()));
        completedTransactions.setText(String.valueOf(report.completedTransactions()));
        monthlyPassCount.setText(String.valueOf(lot.getMonthlyPassCount()));
    }

    private void populateTickets(List<Ticket> tickets) {
        tableModel.setRowCount(0);
        for (Ticket ticket : tickets) {
            tableModel.addRow(new Object[]{ticket.getTicketId(), ticket.getVehicle().getVehicleNumber(),
                    ticket.getVehicle().getOwnerName(), ticket.getVehicle().getVehicleType().replace('_', '-'),
                    ticket.getSlotNumber(), ticket.getEntryTime().format(TIME_FORMAT)});
        }
    }

    private void renderSlots(JPanel panel, boolean[] slots, String prefix) {
        panel.removeAll();
        for (int i = 0; i < slots.length; i++) {
            JLabel slot = new JLabel(prefix + "-" + String.format("%02d", i + 1), SwingConstants.CENTER);
            slot.setOpaque(true); slot.setForeground(Color.WHITE); slot.setFont(new Font("SansSerif", Font.BOLD, 13));
            slot.setBorder(new EmptyBorder(16, 8, 16, 8));
            slot.setBackground(slots[i] ? new Color(190, 67, 67) : new Color(39, 143, 101));
            panel.add(slot);
        }
        panel.revalidate(); panel.repaint();
    }

    private void showMessage(String message, String title, int type) {
        JOptionPane.showMessageDialog(this, message, title, type);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ParkingApp().setVisible(true));
    }
}
