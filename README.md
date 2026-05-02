# README.md

```markdown
# 🅿️ Parking Slot Management System

A Java console application that manages shopping mall parking
for two-wheelers and four-wheelers using OOP, inheritance, and file handling.

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
├── Main.java             → Menu-driven entry point
├── parking_data.txt      → Active parking records
└── parking_log.txt       → Completed transaction log
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
# Compile all files
javac *.java

# Run the application
java Main
```

---

## 🖥️ Output Preview

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

- GUI version using Java Swing
- Database integration using JDBC and SQLite
- Monthly pass support for regular users
- Admin dashboard with revenue reports
- Search vehicle by owner name

---

## 📝 Conclusion

This project shows how Java OOP features can solve a real shopping
mall parking management problem. It combines syllabus concepts in one
application and can be extended later with GUI or database support.

---

## 👨‍💻 Author

**Safar Hashim**
Made with ☕ using Core Java
```
