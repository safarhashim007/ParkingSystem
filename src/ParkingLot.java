import java.time.LocalDate;
import java.util.*;

/** Core parking rules, backed by SQLite through ParkingDatabase. */
public class ParkingLot {
    private static final int TW_CAPACITY = 10;
    private static final int FW_CAPACITY = 5;

    private final boolean[] twSlots = new boolean[TW_CAPACITY];
    private final boolean[] fwSlots = new boolean[FW_CAPACITY];
    private final Map<String, Ticket> activeTickets = new HashMap<>();
    private final ParkingDatabase database = new ParkingDatabase();

    public ParkingLot() {
        for (Ticket ticket : database.loadActiveTickets()) {
            activeTickets.put(ticket.getVehicle().getVehicleNumber(), ticket);
            slotsFor(ticket.getVehicle())[ticket.getSlotNumber() - 1] = true;
        }
    }

    public void parkVehicle(Vehicle vehicle) {
        Ticket ticket = parkVehicleAndGetTicket(vehicle);
        if (ticket != null) ticket.printTicket();
    }

    public Ticket parkVehicleAndGetTicket(Vehicle vehicle) {
        String number = vehicle.getVehicleNumber();
        if (activeTickets.containsKey(number)) return null;

        int slot = allocateSlot(slotsFor(vehicle));
        if (slot == -1) return null;

        Ticket ticket = new Ticket(vehicle, slot);
        ticket.setMonthlyPass(database.hasActivePass(number));
        activeTickets.put(number, ticket);
        database.saveActiveTicket(ticket);
        return ticket;
    }

    public void exitVehicle(String vehicleNumber) {
        Ticket ticket = exitVehicleAndGetTicket(vehicleNumber);
        if (ticket != null) ticket.printBill();
    }

    public Ticket exitVehicleAndGetTicket(String vehicleNumber) {
        Ticket ticket = activeTickets.get(vehicleNumber.toUpperCase());
        if (ticket == null) return null;

        ticket.markExit();
        slotsFor(ticket.getVehicle())[ticket.getSlotNumber() - 1] = false;
        activeTickets.remove(ticket.getVehicle().getVehicleNumber());
        database.completeTicket(ticket);
        return ticket;
    }

    public void registerMonthlyPass(String ownerName, String vehicleNumber, String vehicleType, LocalDate expiryDate) {
        database.registerMonthlyPass(ownerName, vehicleNumber.toUpperCase(), vehicleType, expiryDate);
    }

    public List<Ticket> getActiveTickets() {
        return activeTickets.values().stream().sorted(Comparator.comparing(Ticket::getTicketId)).toList();
    }

    public List<Ticket> searchActiveByOwner(String ownerName) {
        String query = ownerName.trim().toLowerCase();
        return getActiveTickets().stream()
                .filter(ticket -> ticket.getVehicle().getOwnerName().toLowerCase().contains(query))
                .toList();
    }

    // Console-mode views retained for the original Main.java interface.
    public void displayParkedVehicles() {
        if (activeTickets.isEmpty()) { System.out.println("No vehicles currently parked."); return; }
        for (Ticket ticket : getActiveTickets()) {
            System.out.printf("%s | %s | %s | %s | Slot %d%n", ticket.getTicketId(),
                    ticket.getVehicle().getVehicleNumber(), ticket.getVehicle().getOwnerName(),
                    ticket.getVehicle().getVehicleType(), ticket.getSlotNumber());
        }
    }

    public void displaySlotStatus() {
        System.out.println("Two-wheeler slots: " + getAvailableTwoWheelerSlots() + " of " + TW_CAPACITY + " available");
        System.out.println("Four-wheeler slots: " + getAvailableFourWheelerSlots() + " of " + FW_CAPACITY + " available");
    }

    public RevenueReport getRevenueReport() { return database.getRevenueReport(); }
    public int getMonthlyPassCount() { return database.getActivePassCount(); }
    public int getAvailableTwoWheelerSlots() { return countAvailable(twSlots); }
    public int getAvailableFourWheelerSlots() { return countAvailable(fwSlots); }
    public boolean[] getTwoWheelerSlots() { return twSlots.clone(); }
    public boolean[] getFourWheelerSlots() { return fwSlots.clone(); }

    private boolean[] slotsFor(Vehicle vehicle) {
        return vehicle.getVehicleType().equals("TWO_WHEELER") ? twSlots : fwSlots;
    }

    private int allocateSlot(boolean[] slots) {
        for (int i = 0; i < slots.length; i++) {
            if (!slots[i]) { slots[i] = true; return i + 1; }
        }
        return -1;
    }

    private int countAvailable(boolean[] slots) {
        int available = 0;
        for (boolean occupied : slots) if (!occupied) available++;
        return available;
    }
}
