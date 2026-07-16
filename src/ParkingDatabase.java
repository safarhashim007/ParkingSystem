import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** SQLite persistence for active tickets, completed transactions, and monthly passes. */
public class ParkingDatabase {
    private static final String URL = "jdbc:sqlite:parking.db";

    public ParkingDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            createTables();
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("SQLite driver is missing. Add lib/sqlite-jdbc-3.50.3.0.jar to the classpath.", e);
        }
    }

    private Connection connect() throws SQLException { return DriverManager.getConnection(URL); }

    private void createTables() {
        String active = "CREATE TABLE IF NOT EXISTS active_tickets (ticket_id TEXT PRIMARY KEY, vehicle_type TEXT NOT NULL, vehicle_number TEXT UNIQUE NOT NULL, owner_name TEXT NOT NULL, slot_number INTEGER NOT NULL, entry_time TEXT NOT NULL, monthly_pass INTEGER NOT NULL)";
        String transactions = "CREATE TABLE IF NOT EXISTS transactions (id INTEGER PRIMARY KEY AUTOINCREMENT, ticket_id TEXT, vehicle_type TEXT, vehicle_number TEXT, owner_name TEXT, slot_number INTEGER, entry_time TEXT, exit_time TEXT, charge REAL)";
        String passes = "CREATE TABLE IF NOT EXISTS monthly_passes (vehicle_number TEXT PRIMARY KEY, owner_name TEXT NOT NULL, vehicle_type TEXT NOT NULL, expires_on TEXT NOT NULL)";
        try (Connection c = connect(); Statement s = c.createStatement()) {
            s.execute(active); s.execute(transactions); s.execute(passes);
        } catch (SQLException e) { throw new IllegalStateException("Could not initialize parking database.", e); }
    }

    public List<Ticket> loadActiveTickets() {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM active_tickets";
        try (Connection c = connect(); Statement s = c.createStatement(); ResultSet r = s.executeQuery(sql)) {
            while (r.next()) {
                Vehicle vehicle = r.getString("vehicle_type").equals("TWO_WHEELER")
                        ? new TwoWheeler(r.getString("vehicle_number"), r.getString("owner_name"))
                        : new FourWheeler(r.getString("vehicle_number"), r.getString("owner_name"));
                Ticket ticket = new Ticket(r.getString("ticket_id"), vehicle, r.getInt("slot_number"), LocalDateTime.parse(r.getString("entry_time")));
                ticket.setMonthlyPass(r.getInt("monthly_pass") == 1);
                tickets.add(ticket);
            }
            return tickets;
        } catch (SQLException e) { throw new IllegalStateException("Could not load active tickets.", e); }
    }

    public void saveActiveTicket(Ticket ticket) {
        String sql = "INSERT INTO active_tickets VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, ticket.getTicketId()); p.setString(2, ticket.getVehicle().getVehicleType());
            p.setString(3, ticket.getVehicle().getVehicleNumber()); p.setString(4, ticket.getVehicle().getOwnerName());
            p.setInt(5, ticket.getSlotNumber()); p.setString(6, ticket.getEntryTime().toString()); p.setInt(7, ticket.isMonthlyPass() ? 1 : 0);
            p.executeUpdate();
        } catch (SQLException e) { throw new IllegalStateException("Could not save parking ticket.", e); }
    }

    public void completeTicket(Ticket ticket) {
        String insert = "INSERT INTO transactions (ticket_id, vehicle_type, vehicle_number, owner_name, slot_number, entry_time, exit_time, charge) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String delete = "DELETE FROM active_tickets WHERE ticket_id = ?";
        try (Connection c = connect()) {
            c.setAutoCommit(false);
            try (PreparedStatement p = c.prepareStatement(insert); PreparedStatement d = c.prepareStatement(delete)) {
                p.setString(1, ticket.getTicketId()); p.setString(2, ticket.getVehicle().getVehicleType());
                p.setString(3, ticket.getVehicle().getVehicleNumber()); p.setString(4, ticket.getVehicle().getOwnerName());
                p.setInt(5, ticket.getSlotNumber()); p.setString(6, ticket.getEntryTime().toString());
                p.setString(7, LocalDateTime.now().toString()); p.setDouble(8, ticket.calculateCharge()); p.executeUpdate();
                d.setString(1, ticket.getTicketId()); d.executeUpdate(); c.commit();
            } catch (SQLException e) { c.rollback(); throw e; }
        } catch (SQLException e) { throw new IllegalStateException("Could not complete parking transaction.", e); }
    }

    public void registerMonthlyPass(String owner, String vehicleNumber, String vehicleType, LocalDate expiry) {
        String sql = "INSERT INTO monthly_passes VALUES (?, ?, ?, ?) ON CONFLICT(vehicle_number) DO UPDATE SET owner_name=excluded.owner_name, vehicle_type=excluded.vehicle_type, expires_on=excluded.expires_on";
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, vehicleNumber); p.setString(2, owner); p.setString(3, vehicleType); p.setString(4, expiry.toString()); p.executeUpdate();
        } catch (SQLException e) { throw new IllegalStateException("Could not save monthly pass.", e); }
    }

    public boolean hasActivePass(String vehicleNumber) {
        String sql = "SELECT 1 FROM monthly_passes WHERE vehicle_number = ? AND expires_on >= ?";
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, vehicleNumber); p.setString(2, LocalDate.now().toString());
            try (ResultSet r = p.executeQuery()) { return r.next(); }
        } catch (SQLException e) { throw new IllegalStateException("Could not check monthly pass.", e); }
    }

    public int getActivePassCount() {
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement("SELECT COUNT(*) FROM monthly_passes WHERE expires_on >= ?")) {
            p.setString(1, LocalDate.now().toString()); try (ResultSet r = p.executeQuery()) { return r.next() ? r.getInt(1) : 0; }
        } catch (SQLException e) { throw new IllegalStateException("Could not count monthly passes.", e); }
    }

    public RevenueReport getRevenueReport() {
        String sql = "SELECT COALESCE(SUM(charge), 0), COALESCE(SUM(CASE WHEN date(exit_time) = date('now','localtime') THEN charge ELSE 0 END), 0), COALESCE(SUM(CASE WHEN strftime('%Y-%m', exit_time) = strftime('%Y-%m','now','localtime') THEN charge ELSE 0 END), 0), COUNT(*) FROM transactions";
        try (Connection c = connect(); Statement s = c.createStatement(); ResultSet r = s.executeQuery(sql)) {
            return new RevenueReport(r.getDouble(1), r.getDouble(2), r.getDouble(3), r.getInt(4));
        } catch (SQLException e) { throw new IllegalStateException("Could not generate revenue report.", e); }
    }
}
