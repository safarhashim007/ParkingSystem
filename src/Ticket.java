// Ticket.java
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Ticket {
    private static int counter = 1; // auto-increment ticket ID

    private String        ticketId;
    private Vehicle       vehicle;
    private int           slotNumber;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    // Constructor for new parking entry
    public Ticket(Vehicle vehicle, int slotNumber) {
        this.ticketId   = "TKT-" + String.format("%04d", counter++);
        this.vehicle    = vehicle;
        this.slotNumber = slotNumber;
        this.entryTime  = LocalDateTime.now();
    }

    // Constructor used when loading from file
    public Ticket(String ticketId, Vehicle vehicle, int slotNumber,
                  LocalDateTime entryTime) {
        this.ticketId   = ticketId;
        this.vehicle    = vehicle;
        this.slotNumber = slotNumber;
        this.entryTime  = entryTime;
    }

    // Calculate charge using the Vehicle's own rate definitions
    public double calculateCharge() {
        LocalDateTime end = (exitTime != null) ? exitTime : LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(entryTime, end);

        // Minimum 1 hour billing; round up partial hours
        long hours = (long) Math.ceil(minutes / 60.0);
        if (hours < 1) hours = 1;

        double base  = vehicle.getBaseRate();
        double extra = vehicle.getExtraRatePerHour() * (hours - 1);
        return base + extra;
    }

    public void markExit() { this.exitTime = LocalDateTime.now(); }

    // For saving to file — a simple CSV line
    public String toFileString() {
        return ticketId + "," +
                vehicle.getVehicleType() + "," +
                vehicle.getVehicleNumber() + "," +
                vehicle.getOwnerName() + "," +
                slotNumber + "," +
                entryTime.format(FMT);
    }

    // Rebuild a Ticket from a CSV line
    public static Ticket fromFileString(String line) {
        String[] p = line.split(",", 6);
        // p[0]=ticketId, p[1]=type, p[2]=vehicleNo, p[3]=owner,
        // p[4]=slot, p[5]=entryTime
        Vehicle v;
        if (p[1].equals("TWO_WHEELER")) {
            v = new TwoWheeler(p[2], p[3]);
        } else {
            v = new FourWheeler(p[2], p[3]);
        }
        LocalDateTime entry = LocalDateTime.parse(p[5], FMT);
        // Update counter so new tickets don't clash
        int num = Integer.parseInt(p[0].replace("TKT-", ""));
        if (num >= counter) counter = num + 1;
        return new Ticket(p[0], v, Integer.parseInt(p[4]), entry);
    }

    // Display a formatted ticket
    public void printTicket() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║         PARKING TICKET               ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.printf ("║  Ticket ID  : %-23s║%n", ticketId);
        System.out.printf ("║  Vehicle No : %-23s║%n", vehicle.getVehicleNumber());
        System.out.printf ("║  Owner      : %-23s║%n", vehicle.getOwnerName());
        System.out.printf ("║  Type       : %-23s║%n", vehicle.getVehicleType());
        System.out.printf ("║  Slot       : %-23s║%n", slotNumber);
        System.out.printf ("║  Entry Time : %-23s║%n", entryTime.format(FMT));
        System.out.println("╚══════════════════════════════════════╝");
    }

    public void printBill() {
        double charge = calculateCharge();
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║           PARKING BILL               ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.printf ("║  Ticket ID  : %-23s║%n", ticketId);
        System.out.printf ("║  Vehicle No : %-23s║%n", vehicle.getVehicleNumber());
        System.out.printf ("║  Entry Time : %-23s║%n", entryTime.format(FMT));
        System.out.printf ("║  Exit Time  : %-23s║%n", exitTime.format(FMT));
        System.out.printf ("║  Total Amt  : Rs. %-20.2f║%n", charge);
        System.out.println("╚══════════════════════════════════════╝");
    }

    // Getters
    public String        getTicketId()   { return ticketId; }
    public Vehicle       getVehicle()    { return vehicle; }
    public int           getSlotNumber() { return slotNumber; }
    public LocalDateTime getEntryTime()  { return entryTime; }
}