// Main.java
import java.util.Scanner;

public class Main {
    private static Scanner     sc  = new Scanner(System.in);
    private static ParkingLot  lot = new ParkingLot();

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║   PARKING SLOT MANAGEMENT SYSTEM     ║");
        System.out.println("╚══════════════════════════════════════╝");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter choice: ");

            switch (choice) {
                case 1 -> parkVehicleFlow();
                case 2 -> exitVehicleFlow();
                case 3 -> lot.displayParkedVehicles();
                case 4 -> lot.displaySlotStatus();
                case 5 -> {
                    System.out.println("Goodbye! Parking data saved.");
                    running = false;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
        sc.close();
    }

    private static void printMenu() {
        System.out.println("\n┌──────────────────────────────┐");
        System.out.println("│           MAIN MENU          │");
        System.out.println("├──────────────────────────────┤");
        System.out.println("│  1. Park a Vehicle           │");
        System.out.println("│  2. Exit a Vehicle           │");
        System.out.println("│  3. View Parked Vehicles     │");
        System.out.println("│  4. View Slot Availability   │");
        System.out.println("│  5. Exit System              │");
        System.out.println("└──────────────────────────────┘");
    }

    private static void parkVehicleFlow() {
        System.out.println("\n  Vehicle Type:");
        System.out.println("  1 → Two-Wheeler");
        System.out.println("  2 → Four-Wheeler");
        int type = readInt("  Choose: ");

        System.out.print("  Owner Name   : ");
        String name = sc.nextLine().trim();

        System.out.print("  Vehicle Num  : ");
        String num = sc.nextLine().trim();

        Vehicle vehicle;
        if (type == 1) {
            vehicle = new TwoWheeler(num, name);
        } else if (type == 2) {
            vehicle = new FourWheeler(num, name);
        } else {
            System.out.println("Invalid vehicle type.");
            return;
        }
        lot.parkVehicle(vehicle);
    }

    private static void exitVehicleFlow() {
        System.out.print("\n  Enter Vehicle Number: ");
        String num = sc.nextLine().trim();
        lot.exitVehicle(num);
    }

    // Safe integer reader (handles bad input)
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int val = Integer.parseInt(sc.nextLine().trim());
                return val;
            } catch (NumberFormatException e) {
                System.out.println("  ⚠ Please enter a valid number.");
            }
        }
    }
}