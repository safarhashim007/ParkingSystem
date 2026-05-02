// ParkingLot.java
import java.io.*;
import java.util.*;

public class ParkingLot {
    private static final int TW_CAPACITY = 10; // 10 two-wheeler slots
    private static final int FW_CAPACITY =  5; //  5 four-wheeler slots

    private static final String DATA_FILE = "parking_data.txt";
    private static final String LOG_FILE  = "parking_log.txt";

    private boolean[] twSlots; // true = occupied
    private boolean[] fwSlots;
    private Map<String, Ticket> activeTickets; // key = vehicle number

    public ParkingLot() {
        twSlots       = new boolean[TW_CAPACITY];
        fwSlots       = new boolean[FW_CAPACITY];
        activeTickets = new HashMap<>();
        loadFromFile(); // restore state on startup
    }

    // ─── PARK VEHICLE ────────────────────────────────────────────
    public void parkVehicle(Vehicle vehicle) {
        String vNum = vehicle.getVehicleNumber();

        if (activeTickets.containsKey(vNum)) {
            System.out.println("⚠ Vehicle " + vNum + " is already parked!");
            return;
        }

        boolean[] slots = vehicle.getVehicleType().equals("TWO_WHEELER")
                ? twSlots : fwSlots;
        int slotNum = allocateSlot(slots);

        if (slotNum == -1) {
            System.out.println("✗ No slots available for " + vehicle.getVehicleType());
            return;
        }

        Ticket ticket = new Ticket(vehicle, slotNum);
        activeTickets.put(vNum, ticket);
        saveToFile();

        System.out.println("✔ Vehicle parked successfully!");
        ticket.printTicket();
    }

    // ─── EXIT VEHICLE ────────────────────────────────────────────
    public void exitVehicle(String vehicleNumber) {
        vehicleNumber = vehicleNumber.toUpperCase();
        Ticket ticket = activeTickets.get(vehicleNumber);

        if (ticket == null) {
            System.out.println("✗ No active parking found for: " + vehicleNumber);
            return;
        }

        ticket.markExit();

        // Free the slot
        boolean[] slots = ticket.getVehicle().getVehicleType().equals("TWO_WHEELER")
                ? twSlots : fwSlots;
        slots[ticket.getSlotNumber() - 1] = false;

        activeTickets.remove(vehicleNumber);

        ticket.printBill();
        logTransaction(ticket);
        saveToFile();
    }

    // ─── DISPLAY PARKED VEHICLES ─────────────────────────────────
    public void displayParkedVehicles() {
        if (activeTickets.isEmpty()) {
            System.out.println("  No vehicles currently parked.");
            return;
        }
        System.out.println("\n┌─────────┬────────────┬──────────────┬───────────────┬──────┐");
        System.out.println("│ Ticket  │ Vehicle No │ Owner        │ Type          │ Slot │");
        System.out.println("├─────────┼────────────┼──────────────┼───────────────┼──────┤");
        for (Ticket t : activeTickets.values()) {
            System.out.printf("│ %-7s │ %-10s │ %-12s │ %-13s │ %-4d │%n",
                    t.getTicketId(),
                    t.getVehicle().getVehicleNumber(),
                    t.getVehicle().getOwnerName(),
                    t.getVehicle().getVehicleType(),
                    t.getSlotNumber());
        }
        System.out.println("└─────────┴────────────┴──────────────┴───────────────┴──────┘");
    }

    // ─── DISPLAY SLOT STATUS ─────────────────────────────────────
    public void displaySlotStatus() {
        System.out.println("\n  ── TWO-WHEELER SLOTS ──");
        printSlotGrid(twSlots, "TW");
        System.out.println("\n  ── FOUR-WHEELER SLOTS ──");
        printSlotGrid(fwSlots, "FW");
        System.out.printf("%n  [■ Occupied]  [□ Free]%n");
    }

    // ─── HELPERS ─────────────────────────────────────────────────

    private int allocateSlot(boolean[] slots) {
        for (int i = 0; i < slots.length; i++) {
            if (!slots[i]) {
                slots[i] = true;
                return i + 1; // 1-indexed
            }
        }
        return -1; // full
    }

    private void printSlotGrid(boolean[] slots, String prefix) {
        for (int i = 0; i < slots.length; i++) {
            System.out.printf("  %s-%02d[%s]",
                    prefix, i + 1, slots[i] ? "■" : "□");
            if ((i + 1) % 5 == 0) System.out.println();
        }
        System.out.println();
    }

    // ─── FILE HANDLING ───────────────────────────────────────────

    // Save active tickets to DATA_FILE
    private void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_FILE))) {
            for (Ticket t : activeTickets.values()) {
                pw.println(t.toFileString());
            }
        } catch (IOException e) {
            System.out.println("⚠ Could not save data: " + e.getMessage());
        }
    }

    // Load active tickets from DATA_FILE on startup
    private void loadFromFile() {
        File f = new File(DATA_FILE);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Ticket t = Ticket.fromFileString(line);
                activeTickets.put(t.getVehicle().getVehicleNumber(), t);
                // Mark slot as occupied
                if (t.getVehicle().getVehicleType().equals("TWO_WHEELER")) {
                    twSlots[t.getSlotNumber() - 1] = true;
                } else {
                    fwSlots[t.getSlotNumber() - 1] = true;
                }
            }
            System.out.println("✔ Restored " + activeTickets.size()
                    + " active parking record(s) from file.");
        } catch (IOException e) {
            System.out.println("⚠ Could not load data: " + e.getMessage());
        }
    }

    // Append completed transaction to LOG_FILE
    private void logTransaction(Ticket ticket) {
        try (PrintWriter pw = new PrintWriter(
                new FileWriter(LOG_FILE, true))) { // append mode
            pw.printf("EXIT | %s | %s | Charge: Rs.%.2f%n",
                    ticket.toFileString(),
                    java.time.LocalDateTime.now(),
                    ticket.calculateCharge());
        } catch (IOException e) {
            System.out.println("⚠ Could not write log: " + e.getMessage());
        }
    }
}