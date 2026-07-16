# 🅿️ Parking Slot Management System

> **Desktop UI available:** Run `ParkingApp.java` to launch the Swing interface, or run `Main.java` for the original console version.

A Java parking-management application with both a Swing desktop interface and
a console mode. It manages two-wheelers and four-wheelers using OOP,
inheritance, and file handling.

## ▶️ Run the Desktop App

Compile and run the project with a JDK (Java 17 or newer recommended). The included SQLite driver stores records in `parking.db`:

```bash
javac -cp "lib/sqlite-jdbc-3.50.3.0.jar" -d out src/*.java
java -cp "out:lib/sqlite-jdbc-3.50.3.0.jar" ParkingApp
```

To use the original console application instead:

```bash
java -cp "out:lib/sqlite-jdbc-3.50.3.0.jar" Main
```

---

## 🚨 Problem Statement

A busy shopping mall handles hundreds of vehicles daily across
two-wheeler and four-wheeler parking zones. Manual entry in registers
causes double slot allocation, billing disputes, and revenue leakage
with no way to track or audit records. This project digitizes that task
using core Java features like OOP, inheritance, and file handling.

---

## ✨ Features

- 🏍️ Park two-wheelers and 🚗 four-wheelers with automatic slot allocation
- 🎫 Generate parking ticket with entry time and slot number
- 💰 Calculate charges automatically based on parking duration
- 📋 View all currently parked vehicles
- 🟩 Check real-time slot availability
- 💾 Save and load records using file handling
- 📝 Maintain transaction log for audit purposes
- 🖥️ Use a Java Swing desktop interface for parking, checkout, and live availability
- 🗃️ Persist active tickets, transactions, and monthly passes in SQLite
- 🔎 Search active vehicles by owner name
- 📊 View an admin dashboard with daily, monthly, and total revenue
- 🪪 Register monthly passes for regular users

---

## 💵 Parking Rates

| Vehicle Type | First Hour | Every Additional Hour |
|---|---|---|
| 🏍️ Two-Wheeler | ₹10 | ₹5 |
| 🚗 Four-Wheeler | ₹20 | ₹10 |

---

## 🗂️ Project Structure

```
ParkingSystem/
├── Vehicle.java          → Abstract base class
├── TwoWheeler.java       → Extends Vehicle
├── FourWheeler.java      → Extends Vehicle
├── Ticket.java           → Parking ticket and billing logic
├── ParkingLot.java       → Core logic and file handling
├── ParkingApp.java       → Swing desktop interface
├── ParkingDatabase.java  → JDBC and SQLite persistence
├── RevenueReport.java    → Admin dashboard data model
├── lib/                  → SQLite JDBC driver
├── Main.java             → Menu-driven entry point
└── parking.db            → Local SQLite database (generated at runtime)
```

---

## 🧠 OOP Concepts Used

| Concept | Where Applied |
|---|---|
| Abstraction | `Vehicle` is an abstract class |
| Inheritance | `TwoWheeler` and `FourWheeler` extend `Vehicle` |
| Polymorphism | `getBaseRate()` overridden in each subclass |
| Encapsulation | All fields are private, accessed via getters |

---

## 📚 Syllabus Mapping

| Module | Concepts Used |
|---|---|
| Module 1 | `switch`, `while`, `if-else`, input validation |
| Module 2 | Classes, constructors, `static`, `final` |
| Module 3 | Inheritance, method overriding |
| Module 4 | Interface, thread-based report generation |
| Module 5 | Custom exception, file I/O, `ArrayList`, `HashMap` |

---

## ▶️ How to Run

```bash
# Compile the project
javac -cp "lib/sqlite-jdbc-3.50.3.0.jar" -d out src/*.java

# Launch the desktop application
java -cp "out:lib/sqlite-jdbc-3.50.3.0.jar" ParkingApp
```

---

## 🖥️ Desktop Interface

The Swing interface provides a dashboard with live slot availability, vehicle
parking and checkout forms, owner search, monthly-pass registration, and an
admin dashboard with transaction and revenue totals.

---

## ⌨️ Console Output Preview

```
╔══════════════════════════════════════╗
║   PARKING SLOT MANAGEMENT SYSTEM     ║
╚══════════════════════════════════════╝

┌──────────────────────────────┐
│           MAIN MENU          │
├──────────────────────────────┤
│  1. Park a Vehicle           │
│  2. Exit a Vehicle           │
│  3. View Parked Vehicles     │
│  4. View Slot Availability   │
│  5. Exit System              │
└──────────────────────────────┘
```

---

## 🔮 Future Enhancements

- Role-based admin login
- Export admin reports as CSV or PDF

---

## 📝 Conclusion

This project shows how Java OOP features can solve a real shopping
mall parking management problem. It combines syllabus concepts in one
application with a desktop UI and a persistent SQLite database.

---

## 👨‍💻 Author

**Safar Hashim**
Made with ☕ using Core Java
