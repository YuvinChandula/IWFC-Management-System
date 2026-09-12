# 🏋️‍♂️ Intelligent Wellness and Fitness Center (IWFC) Management System



## 📖 Table of Contents
- [Project Overview](#-project-overview)
- [Key Features](#-key-features)
- [System Architecture](#-system-architecture)
- [Gang of Four (GoF) Design Patterns](#-gang-of-four-gof-design-patterns)
- [Object-Oriented Programming Principles (LO1)](#-object-oriented-programming-principles-lo1)
- [Generics & Collections (LO2)](#-generics--collections-lo2)
- [Security & Custom Exception Handling (LO3)](#-security--custom-exception-handling-lo3)
- [User Roles & Access Control Matrix](#-user-roles--access-control-matrix)
- [Automated Unit Testing](#-automated-unit-testing)
- [Project Directory Structure](#-project-directory-structure)
- [Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [Compilation & Execution](#compilation--execution)
- [Assessment Deliverables](#-assessment-deliverables)
- [License](#-license)

---

## 📌 Project Overview
The **Intelligent Wellness and Fitness Center (IWFC)** is an enterprise-grade Java software platform engineered to replace fragmented manual gym operations with a unified digital tool. It resolves three critical facility management challenges:
1. **Unmonitored Equipment Wear:** Tracks cumulative session hours to trigger automated preventative maintenance alerts before machinery suffers mechanical breakdown.
2. **Studio & Equipment Collisions:** Strictly eliminates double-bookings through mathematical time-interval intersection validation.
3. **Delayed Fault Reporting:** Accelerates repair lifecycles via an event-driven Observer pattern that broadcasts instant notifications to staff terminals and the desktop GUI.

---

## 🌟 Key Features

### 1. Equipment Lifecycle & Preventative Maintenance
- **Inventory Governance:** Add, update, view, and deactivate fitness equipment across zones (Cardio Zone, Studio A, Resistance Floor).
- **Cumulative Wear Tracking:** Automatically accumulates operating hours from completed workout sessions.
- **Dynamic Threshold Alerts:** Flags equipment requiring preventative servicing once cumulative hours reach category thresholds (`requiresPreventativeMaintenance()`).

### 2. Timetable Scheduling & Collision Prevention
- **Strict Double-Booking Interception:** Evaluates proposed class intervals `[StartA, EndA]` against existing bookings `[StartB, EndB]` using interval intersection:
  $$\text{Collision occurs if: } (\text{Start}_A < \text{End}_B) \land (\text{End}_A > \text{Start}_B)$$
- **Resource Constraints:** Validates both studio rooms and assigned equipment simultaneously.
- **Operating Hours Enforcement:** Blocks classes scheduled outside gym operating hours (06:00 to 22:00).
- **Capacity Management:** Prevents class overbooking and duplicate member registrations.

### 3. Maintenance Ticket Workflow & Real-Time Alerts
- **Fault Reporting:** Staff log technical defects with urgency ratings (`LOW`, `MEDIUM`, `HIGH`).
- **State Lifecycle:** Tickets transition through deterministic states: `PENDING` $\rightarrow$ `ASSIGNED` $\rightarrow$ `COMPLETED`.
- **Live Event Dispatch:** State transitions instantly trigger notifications to all registered observers without polling loops.

---

## 🏛️ System Architecture
The application adheres to a decoupled, multi-tier layered architecture:

```
┌─────────────────────────────────────────────────────────────┐
│                 1. PRESENTATION TIER (UI)                   │
│      Main.java (Console CLI)  |  IWFCMainFrame (Swing GUI)  │
└──────────────────────────────┬──────────────────────────────┘
                               │ delegates to
┌──────────────────────────────▼──────────────────────────────┐
│           2. STRUCTURAL FACADE (Security & RBAC)            │
│       IWFCFacade.java (Central Access & checkRole)          │
└──────────────────────────────┬──────────────────────────────┘
                               │ coordinates
┌──────────────────────────────▼──────────────────────────────┐
│                  3. DOMAIN SERVICE LAYER                    │
│   EquipmentService   |   SessionService   |  MaintenanceSvc │
└──────────────────────────────┬──────────────────────────────┘
                               │ persists in
┌──────────────────────────────▼──────────────────────────────┐
│              4. GENERIC DATA ACCESS LAYER (LO2)             │
│   Repository<T> using HashMap<String, T> & Function<T,String>│
└──────────────────────────────┬──────────────────────────────┘
                               │ manages
┌──────────────────────────────▼──────────────────────────────┐
│                     5. DOMAIN MODELS                        │
│   User  |  Equipment  |  Session  |  MaintenanceRequest    │
└─────────────────────────────────────────────────────────────┘
```

---

## 🎨 Gang of Four (GoF) Design Patterns

| Pattern Category | Pattern Applied | Implementation Class | Architectural Problem Solved |
| :--- | :--- | :--- | :--- |
| **Creational** | **Factory Method** | `EquipmentFactory` | Centralises equipment creation and automatically injects category-specific maintenance threshold hours (Treadmill: 150h, Spin Bike: 100h, Rower: 120h, Default: 200h). |
| **Structural** | **Facade Pattern** | `IWFCFacade` | Provides a unified, single API entry point that decouples the UI from domain services while centralising Role-Based Access Control (RBAC). |
| **Behavioural** | **Observer Pattern** | `MaintenanceObserver`, `StaffNotificationListener`, `IWFCMainFrame` | Implements a one-to-many subscription model broadcasting real-time ticket state changes to console logs and the desktop dashboard without polling. |

---

## 🧱 Object-Oriented Programming Principles (LO1)
- **Encapsulation:** All model instance fields are declared `private`. Internal state is accessed via getters, guarded state transitions (e.g., `addUsage` rejecting negative inputs), and defensive copies (`Collections.unmodifiableList` for attendees).
- **Abstraction:** The `MaintenanceObserver` interface exposes only the `onStatusUpdate` contract, hiding concrete presentation mechanics from domain controllers.
- **Inheritance:** Custom exception types extend `java.lang.Exception`, inheriting call stack tracing and message formatting.
- **Polymorphism:** `MaintenanceService` invokes `onStatusUpdate` polymorphically; the call dispatches dynamically to `StaffNotificationListener` (console log) and `IWFCMainFrame` (GUI badge update).

---

## 🗄️ Generics & Collections (LO2)
- **Universal Generic Repository:** `Repository<T>` parameterised with type variable `<T>`, providing compile-time type safety across all entity types without code duplication.
- **Dynamic Key Extraction:** Parameterised with `Function<T, String> keyExtractor` (e.g., `Equipment::getId`, `Session::getSessionId`).
- **Algorithmic Efficiency:**
  - `HashMap<String, T>`: Delivers average-case $O(1)$ constant-time retrieval, insertion, and existence verification by primary key.
  - `ArrayList<T>`: Maintained for ordered attendees and observer subscriber lists.

---

## 🛡️ Security & Custom Exception Handling (LO3)

```
                       java.lang.Exception
                                ▲
        ┌───────────────────────┼───────────────────────┐
        │                       │                       │
DuplicateDataException  InvalidBookingException  UnauthorizedAccessException
```

- **`DuplicateDataException`:** Raised by `Repository.save()` when an existing primary key collision is detected, preventing silent data corruption.
- **`InvalidBookingException`:** Raised by `SessionService` when a class booking creates a studio or machine time clash, exceeds room capacity, or falls outside operating hours (06:00–22:00).
- **`UnauthorizedAccessException`:** Raised by `IWFCFacade.checkRole()` when an actor attempts an action outside their role privileges (e.g., a Member attempting equipment registration).

---

## 👥 User Roles & Access Control Matrix

| System Action | 👑 Administrator | 🏋️ Instructor | 🏃 Member | Exception Thrown on Breach |
| :--- | :---: | :---: | :---: | :--- |
| **Register New Equipment** | ✅ | ❌ | ❌ | `UnauthorizedAccessException` |
| **Schedule Class Timetable** | ✅ | ✅ | ❌ | `UnauthorizedAccessException` |
| **Report Equipment Fault** | ✅ | ✅ | ❌ | `UnauthorizedAccessException` |
| **Assign Maintenance Tech** | ✅ | ❌ | ❌ | `UnauthorizedAccessException` |
| **Complete Maintenance Work** | ✅ | ❌ | ❌ | `UnauthorizedAccessException` |
| **View Class Timetables** | ✅ | ✅ | ✅ | Allowed |
| **Book Fitness Class Slot** | ❌ | ❌ | ✅ | `UnauthorizedAccessException` |

### Pre-Configured Test Accounts:
- **Alice Admin:** ID `ADM-01` | Role: `ADMIN`
- **Bob Instructor:** ID `INS-01` | Role: `INSTRUCTOR`
- **Charlie Member:** ID `MEM-01` | Role: `MEMBER`

---

## 🧪 Automated Unit Testing

The test suite is built on **JUnit 5 Jupiter** and executed through the **Maven Surefire** runner:
```bash
mvn test
```

### Test Suite Coverage:
1. **`SessionServiceTest`**: Validates successful session reservations, capacity tracking, and verifies that overlapping studio bookings throw `InvalidBookingException`.
2. **`MaintenanceServiceTest`**: Validates the complete lifecycle state machine (`PENDING` $\rightarrow$ `ASSIGNED` $\rightarrow$ `COMPLETED`) and verifies that registered observers receive event callbacks.
3. **`CustomExceptionTest` (Intentional Negative Tests)**: Mandated by the assessment brief; uses `assertThrows` to prove that `UnauthorizedAccessException` and `DuplicateDataException` trigger under invalid input conditions.

```
Results:
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 📁 Project Directory Structure
```
IWFC Management System/
├── src/
│   ├── main/java/com/iwfc/
│   │   ├── exception/                  # Custom Checked Exceptions (LO3)
│   │   │   ├── DuplicateDataException.java
│   │   │   ├── InvalidBookingException.java
│   │   │   └── UnauthorizedAccessException.java
│   │   ├── model/                      # Domain Entities & Strongly-Typed Enums
│   │   │   ├── Equipment.java
│   │   │   ├── EquipmentStatus.java
│   │   │   ├── MaintenanceRequest.java
│   │   │   ├── Session.java
│   │   │   ├── User.java
│   │   │   └── UserRole.java
│   │   ├── pattern/                    # Gang of Four Design Patterns
│   │   │   ├── creational/
│   │   │   │   └── EquipmentFactory.java       # Factory Method Pattern
│   │   │   ├── structural/
│   │   │   │   └── IWFCFacade.java             # Facade Pattern & RBAC
│   │   │   └── behavioural/
│   │   │       ├── MaintenanceObserver.java    # Observer Interface
│   │   │       └── StaffNotificationListener.java # Concrete Observer
│   │   ├── repository/
│   │   │   └── Repository.java                 # Generic In-Memory Repository<T> (LO2)
│   │   ├── service/                    # Business Service Controllers
│   │   │   ├── EquipmentService.java
│   │   │   ├── MaintenanceService.java
│   │   │   └── SessionService.java
│   │   ├── ui/
│   │   │   └── IWFCMainFrame.java              # 4-Tab Desktop GUI (Java Swing)
│   │   └── Main.java                   # Bootstrap Entry Point & Seed Data
│   └── test/java/com/iwfc/             # Automated JUnit 5 Test Suite
│       ├── CustomExceptionTest.java    # Intentional Failing Assertion Tests
│       ├── MaintenanceServiceTest.java # Maintenance Lifecycle Tests
│       └── SessionServiceTest.java     # Scheduling & Conflict Tests
├── pom.xml                             # Apache Maven Build Specification
├── .gitignore                          # Git Exclusion Rules
└── README.md                           # Project Documentation
```

---

## 🚀 Getting Started

### Prerequisites
- **Java Development Kit (JDK):** Version 17 or higher (Verified on OpenJDK 25)
- **Apache Maven:** Version 3.8 or higher
- **Git:** Version 2.30 or higher

### Compilation & Execution

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/YuvinChandula/IWFC-Management-System.git
   cd "IWFC-Management-System"
   ```

2. **Compile Project Code:**
   ```bash
   mvn clean compile
   ```

3. **Launch Application (Desktop GUI & Console):**
   ```bash
   java -cp target/classes com.iwfc.Main
   ```
   *Or using Maven:*
   ```bash
   mvn exec:java -Dexec.mainClass="com.iwfc.Main"
   ```

4. **Execute Automated Tests:**
   ```bash
   mvn test
   ```

---

