# The Grand Azure Hotel & Suites - Enterprise Web-based Hotel Reservation System

[![Java Version](https://img.shields.io/badge/Java-17%20LTS-007396?logo=java&logoColor=white)](https://www.oracle.com/java/)
[![Servlet API](https://img.shields.io/badge/Servlet-4.0%20(Tomcat%209)-F8DC75?logo=apachetomcat&logoColor=black)](https://tomcat.apache.org/)
[![Database](https://img.shields.io/badge/Database-MongoDB%20Community%20Server-47A248?logo=mongodb&logoColor=white)](https://www.mongodb.com/)
[![License](https://img.shields.io/badge/Architecture-MVC%20Enterprise-blue)]()

A complete, production-grade, and responsive web-based **Hotel Reservation System** built using **Java 17**, **Java Servlets (`javax.servlet.*`)**, **JavaServer Pages (JSP)**, **JSTL**, and **MongoDB**. Designed and structured for a Bachelor of Engineering in Computer Science capstone / term project.

---

## Table of Contents

1. [Project Overview & Key Features](#1-project-overview--key-features)
2. [System Architecture & MVC Design](#2-system-architecture--mvc-design)
3. [Technology Stack](#3-technology-stack)
4. [MongoDB Database Design & Indexes](#4-mongodb-database-design--indexes)
5. [Visual Database Management with MongoDB Compass](#5-visual-database-management-with-mongodb-compass)
6. [Business Logic & Concurrency Control](#6-business-logic--concurrency-control)
7. [Security & Robustness](#7-security--robustness)
8. [Project Directory Structure](#8-project-directory-structure)
9. [Prerequisites & Environment Setup](#9-prerequisites--environment-setup)
10. [Build, Test, and Deployment Guide](#10-build-test-and-deployment-guide)
11. [Default Credentials & Seed Data](#11-default-credentials--seed-data)
12. [Automated & Manual Integration Testing](#12-automated--manual-integration-testing)
13. [Viva Voce & Technical Defense Preparation](#13-viva-voce--technical-defense-preparation)

---

## 1. Project Overview & Key Features

The **Grand Azure Hotel Reservation System** automates hotel reservations, front-desk operations, room inventory management, and guest record auditing. All persistent data is stored directly in **MongoDB** using BSON documents with strict decimal precision and indexing.

### Customer Capabilities
- **Room Showcase & Browsing**: Explore active rooms and suites with photographs, amenities, room numbers, and nightly pricing loaded dynamically from MongoDB.
- **Availability Search & Filtering**: Filter rooms by check-in / check-out dates, guest count, room category (Standard, Deluxe, Suite, Family), and price bounds.
- **Dynamic Price & Night Estimation**: Real-time JavaScript calculation of nights and estimated subtotal as dates are selected, backed by strict server-side validation.
- **Reservation Workflow**: Review room details, receive itemized cost breakdown (subtotal, lodging taxes, and grand total), review simulated academic demo payment, and confirm booking.
- **Instant Booking Confirmation**: Generates unique confirmation numbers (e.g., `HRS-20261028-UBBP2`) with printable receipts.
- **Customer Dashboard & My Bookings**: View active upcoming stays, completed stays, and cancelled reservations.
- **Self-Service Cancellation**: Cancel eligible upcoming reservations with immediate calendar availability release and ownership enforcement.

### Administrator Capabilities
- **Live Executive Analytics**: Dashboard showing real-time statistics (total rooms, active rooms, total bookings, upcoming reservations, cancelled reservations, registered customers, and aggregated gross revenue).
- **Room Inventory Management**: Add rooms, edit room details (title, description, price, capacity, amenities array, image URL), toggle active/inactive status, and execute safe deletions.
- **Relational Integrity Preservation**: Permanent deletion of rooms with historical reservations is blocked to maintain audit trails.
- **Front-Desk Reservation Management**: Filter bookings by status (`CONFIRMED`, `CHECKED_IN`, `CHECKED_OUT`, `CANCELLED`) or keyword search.
- **Lifecycle Status Transitions**: Transition guests through approved states (`CONFIRMED` &rarr; `CHECKED_IN` &rarr; `CHECKED_OUT`, or `CANCELLED`).
- **Customer Directory**: View registered customer profiles and contact details with password hashes securely redacted.

---

## 2. System Architecture & MVC Design

The project strictly follows the **Model-View-Controller (MVC)** architectural pattern:

```
+-----------------------------------------------------------------------------------+
|                                  USER BROWSER                                     |
+-----------------------------------------------------------------------------------+
       |                                                                     ^
   HTTP Request                                                          HTML / JSP
       v                                                                     |
+-----------------------------------------------------------------------------------+
| CONTROLLER LAYER (Java Servlets)                                                  |
| - HomeServlet, AuthServlet, RoomListServlet, RoomDetailServlet                    |
| - BookingServlet, CustomerDashboardServlet, CustomerBookingsServlet               |
| - AdminDashboardServlet, AdminRoomServlet, AdminBookingServlet                    |
+-----------------------------------------------------------------------------------+
       |                                                                     ^
   Request Delegation                                                    DTO / Model
       v                                                                     |
+-----------------------------------------------------------------------------------+
| SERVICE LAYER (Business Logic & Concurrency Control)                              |
| - AuthService (Registration, BCrypt Hashing, Session Management)                  |
| - RoomService (Catalog Filtering, Inventory Operations, Safety Integrity)         |
| - BookingService (Per-Room ReentrantLock, Overlap Detection, BigDecimal Pricing)  |
| - AdminService (MongoDB Aggregations, Status Lifecycle Validation)                |
+-----------------------------------------------------------------------------------+
       |                                                                     ^
   Entity Operations                                                     BSON Mapping
       v                                                                     |
+-----------------------------------------------------------------------------------+
| DAO / REPOSITORY LAYER (MongoDB Java Sync Driver)                                 |
| - UserDAO, RoomDAO, BookingDAO                                                    |
| - Single Application-wide MongoClient singleton                                   |
+-----------------------------------------------------------------------------------+
       |                                                                     ^
   Mongo Wire Protocol (TCP 27017)                                       BSON Docs
       v                                                                     |
+-----------------------------------------------------------------------------------+
| DATABASE: MongoDB (hotel_reservation_db)                                          |
| Collections: users, rooms, bookings                                               |
+-----------------------------------------------------------------------------------+
```

---

## 3. Technology Stack

| Layer | Component | Version / Specification | Purpose |
| :--- | :--- | :--- | :--- |
| **Language** | Java | Java 17 LTS | Modern language features, records, pattern matching |
| **Web Container** | Apache Tomcat | 9.0.x (`javax.servlet.*`) | Enterprise Servlet 4.0 / JSP 2.3 container |
| **Presentation** | JSP + JSTL + EL | 2.3 / 1.2 | Server-side dynamic HTML rendering |
| **Database** | MongoDB | Community Server 8.x / 7.x / 6.x | Schemaless BSON document persistence |
| **DB Client** | MongoDB Driver | `mongodb-driver-sync` 4.11.1 | Official synchronous Java MongoDB driver |
| **DB Tool** | MongoDB Compass | Latest GUI | Visual inspection, schema verification, query analysis |
| **Security** | jBCrypt | 0.4 | Salted Blowfish password hashing |
| **Build Tool** | Apache Maven | 3.8+ / WAR Packaging | Dependency management & standard deployment |
| **Testing** | JUnit & Mockito | 4.13.2 & 4.11.0 | Unit tests and mock verification |

---

## 4. MongoDB Database Design & Indexes

Database Name: **`hotel_reservation_db`**

### Collection 1: `users`
Stores guest and administrator credentials. Passwords are never stored in plaintext.
```json
{
  "_id": {"$oid": "671fa89b21a8f94d80a1c101"},
  "fullName": "Alexander Vance",
  "email": "alex@example.com",
  "normalizedEmail": "alex@example.com",
  "phone": "+1 (555) 234-5678",
  "passwordHash": "$2a$12$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy",
  "role": "CUSTOMER",
  "createdAt": {"$date": "2026-10-28T08:00:00.000Z"},
  "updatedAt": {"$date": "2026-10-28T08:00:00.000Z"}
}
```
**Indexes**:
- `normalizedEmail` (Unique): Enforces single account registration per email address.

### Collection 2: `rooms`
Stores room specifications, inventory state, and pricing.
```json
{
  "_id": {"$oid": "671fa89b21a8f94d80a1c201"},
  "roomNumber": "101",
  "roomType": "SUITE",
  "title": "Presidential Ocean View Suite",
  "description": "Unrivaled panoramic coastal views with private terrace, Italian marble bath...",
  "pricePerNight": {"$numberDecimal": "495.00"},
  "capacity": 4,
  "amenities": ["King Bed", "Private Terrace", "Oceanfront View", "Jacuzzi", "High-Speed Wi-Fi"],
  "imageUrl": "assets/images/room-suite-1.svg",
  "active": true,
  "createdAt": {"$date": "2026-10-28T08:00:00.000Z"},
  "updatedAt": {"$date": "2026-10-28T08:00:00.000Z"}
}
```
**Indexes**:
- `roomNumber` (Unique): Prevents duplicate room numbers in hotel inventory.

### Collection 3: `bookings`
Stores reservation records, date intervals, and financial snapshots.
```json
{
  "_id": {"$oid": "671fa89b21a8f94d80a1c301"},
  "bookingNumber": "HRS-20261028-UBBP2",
  "userId": {"$oid": "671fa89b21a8f94d80a1c101"},
  "roomId": {"$oid": "671fa89b21a8f94d80a1c201"},
  "checkIn": "2026-11-01",
  "checkOut": "2026-11-04",
  "numberOfGuests": 2,
  "numberOfNights": 3,
  "pricePerNightAtBooking": {"$numberDecimal": "495.00"},
  "subtotal": {"$numberDecimal": "1485.00"},
  "taxes": {"$numberDecimal": "178.20"},
  "totalAmount": {"$numberDecimal": "1663.20"},
  "status": "CONFIRMED",
  "createdAt": {"$date": "2026-10-28T08:30:00.000Z"},
  "updatedAt": {"$date": "2026-10-28T08:30:00.000Z"}
}
```
**Indexes**:
- `bookingNumber` (Unique): Fast confirmation lookup and duplicate prevention.
- `userId`: Index for customer reservation history.
- `roomId`: Index for room reservation history.
- `status`: Index for state filtering.
- Compound Index `(roomId, status, checkIn, checkOut)`: High-performance conflict query for calendar availability.

---

## 5. Visual Database Management with MongoDB Compass

MongoDB Compass is the official graphical user interface (GUI) for MongoDB.

### Connecting Compass to the Project Database
1. Launch **MongoDB Compass**.
2. In the "New Connection" screen, enter the URI:
   ```
   mongodb://localhost:27017
   ```
   *(Or paste your MongoDB Atlas URI if using cloud hosting).*
3. Click **Connect**.
4. In the left navigation pane, select **`hotel_reservation_db`**.
5. You will see the three primary collections:
   - **`users`**
   - **`rooms`**
   - **`bookings`**

### Live Inspection Scenarios in Compass
- **Verify Customer Registration**:
  1. Open Compass &rarr; `hotel_reservation_db` &rarr; `users`.
  2. Register a new user in the web application (`/register`).
  3. Click the **Refresh** button in Compass.
  4. Notice the new user document appears immediately with its salted `passwordHash` (never plaintext) and `role: "CUSTOMER"`.
- **Inspect Live Reservations**:
  1. Confirm a booking in the web browser.
  2. In Compass, open the `bookings` collection and click **Refresh**.
  3. Inspect `bookingNumber`, `pricePerNightAtBooking`, `subtotal`, and `totalAmount` stored as `Decimal128`.
- **Verify Cancellation & Availability Release**:
  1. Cancel a booking from "My Reservations" or the Admin Portal.
  2. Refresh the document in Compass; observe `status: "CANCELLED"` and `cancelledAt` timestamp populated.
  3. Search the room on the website for the same dates: the room is now immediately available again for reservation.

---

## 6. Business Logic & Concurrency Control

### 1. The Hotel Stay Overlap Rule
A room is considered unavailable for a requested stay interval `[requestedCheckIn, requestedCheckOut)` if any existing booking has a status that occupies the room (`CONFIRMED` or `CHECKED_IN`) and satisfies:

$$\text{existingCheckIn} < \text{requestedCheckOut} \quad \text{AND} \quad \text{existingCheckOut} > \text{requestedCheckIn}$$

- **Consecutive Stays (Same-Day Turnover)**: Check-out occurs at 11:00 AM and check-in at 3:00 PM. Therefore, a booking departing on `2026-11-05` and a new booking arriving on `2026-11-05` do **not** overlap.
- **Cancelled Stays**: When a reservation is cancelled, its status changes to `CANCELLED`, which excludes it from the occupancy filter, releasing the dates immediately.

### 2. Single-Instance In-Memory Concurrency Locking
To prevent race conditions where two users attempt to book the last available room for the same dates simultaneously:
- `BookingService` maintains a thread-safe lock registry:
  ```java
  private static final ConcurrentHashMap<String, ReentrantLock> ROOM_LOCKS = new ConcurrentHashMap<>();
  ```
- Before finalizing a booking, the thread acquires the lock for that specific room ID.
- **Availability is re-checked inside the critical section** while holding the lock.
- If a race occurs, the second thread detects that the room was already taken and gracefully rejects the request with an informative message.

> **Architectural Boundary Note (Single-Instance vs. Distributed Clusters):**
> An in-memory Java `ReentrantLock` coordinates threads within a single JVM process (single Tomcat instance). In a multi-node horizontal production cluster behind a reverse proxy/load balancer, single-process locks cannot synchronize across JVM boundaries.
> For horizontal scaling, distributed architectures require either:
> 1. **MongoDB Multi-Document ACID Transactions** on a Replica Set (`startSession()`, `withTransaction()`).
> 2. **Distributed Locks** using Redis (Redlock algorithm) or Apache ZooKeeper.
> 3. **Atomic Conditional Document Updates** using MongoDB's `$addToSet` / `$pull` on an array of booked date intervals.

### 3. Financial Integrity with BigDecimal
Java floating-point types (`float`, `double`) suffer from IEEE-754 binary representation inaccuracies. In this system:
- All monetary operations use `java.math.BigDecimal` with `RoundingMode.HALF_UP` and 2 decimal places.
- MongoDB fields use `Decimal128`.
- Historical price snapshot: `pricePerNightAtBooking` is recorded at the moment of reservation so subsequent admin price changes do not distort historical ledger entries.

---

## 7. Security & Robustness

- **Password Hashing**: Salted BCrypt (log rounds = 12). Plaintext passwords never touch persistent storage or logs.
- **Cross-Site Request Forgery (CSRF)**: `CsrfFilter` generates cryptographically random tokens (`SecureRandom`) bound to the user session. All state-changing POST forms require the `_csrf` token.
- **Role-Based Access Control (RBAC)**:
  - `AuthFilter` protects `/customer/*`, `/book`, and `/booking/*`.
  - `AdminFilter` strictly restricts `/admin/*` to users with `role: ADMIN`. Direct URL navigation by unauthorized users results in HTTP 403 Forbidden.
- **Session Fixation Prevention**: Session is invalidated upon login and a new session ID is generated (`request.getSession(true)`).
- **Ownership Verification**: Customers can only view or cancel reservations belonging to their own user ID (`userId == currentUser.getId()`). Admin users have global audit permissions.
- **NoSQL Injection Defense**: Queries use strongly-typed BSON filter builders (`Filters.eq()`, `Filters.and()`, `Filters.in()`). Untrusted inputs are strictly sanitized and parsed before building queries.
- **Safe HTML Escaping**: `ValidationUtil.sanitize()` and JSTL `<c:out>` prevent Cross-Site Scripting (XSS).
- **Generic Error Pages**: HTTP 403, 404, and 500 error pages mask internal stack traces and database connection strings.

---

## 8. Project Directory Structure

```
hotel-reservation-system/
├── pom.xml                                      # Maven WAR build configuration
├── README.md                                    # Comprehensive system documentation & viva guide
├── .gitignore                                   # Git ignore rules
├── .env.example                                 # Environment variable template
├── build.bat                                    # Automated build script (Maven & JDK direct)
├── test.bat                                     # Automated test runner script
├── run-local-mongo.bat                          # Local MongoDB server runner
├── src/
│   ├── main/
│   │   ├── java/com/hotelreservation/
│   │   │   ├── config/
│   │   │   │   ├── AppConfig.java               # Config reader (env vars & application.properties)
│   │   │   │   ├── AppContextListener.java      # ServletContextListener managing DB lifecycle
│   │   │   │   ├── DatabaseConnection.java      # Single MongoClient singleton provider
│   │   │   │   └── DatabaseInitializer.java     # Index creation & bootstrap seed manager
│   │   │   ├── dao/
│   │   │   │   ├── UserDAO.java                 # MongoDB CRUD & queries for users
│   │   │   │   ├── RoomDAO.java                 # MongoDB CRUD, search & capacity filters for rooms
│   │   │   │   └── BookingDAO.java              # MongoDB bookings, overlap query & revenue aggregation
│   │   │   ├── filter/
│   │   │   │   ├── EncodingFilter.java          # UTF-8 character encoding filter
│   │   │   │   ├── CsrfFilter.java              # CSRF protection validation filter
│   │   │   │   ├── AuthFilter.java              # Customer authentication gatekeeper
│   │   │   │   └── AdminFilter.java             # Administrator RBAC gatekeeper
│   │   │   ├── model/
│   │   │   │   ├── Role.java                    # Enum: CUSTOMER, ADMIN
│   │   │   │   ├── RoomType.java                # Enum: STANDARD, DELUXE, SUITE, FAMILY
│   │   │   │   ├── BookingStatus.java           # Enum: CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED
│   │   │   │   ├── User.java                    # User model
│   │   │   │   ├── Room.java                    # Room model
│   │   │   │   ├── Booking.java                 # Booking model
│   │   │   │   └── dto/
│   │   │   │       ├── RoomSearchCriteria.java  # DTO for search, dates & price filters
│   │   │   │       ├── BookingCostBreakdown.java# DTO for server-side BigDecimal pricing
│   │   │   │       └── AdminDashboardStats.java # DTO for live KPI metrics
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java             # Registration & BCrypt authentication
│   │   │   │   ├── RoomService.java             # Room inventory & availability checks
│   │   │   │   ├── BookingService.java          # Concurrency lock & booking lifecycle
│   │   │   │   └── AdminService.java            # KPI analytics & status transitions
│   │   │   ├── servlet/
│   │   │   │   ├── HomeServlet.java             # Public landing page
│   │   │   │   ├── AuthServlet.java             # Login, register, logout
│   │   │   │   ├── RoomListServlet.java         # Room catalog & availability search
│   │   │   │   ├── RoomDetailServlet.java       # Room specifications & booking controls
│   │   │   │   ├── BookingServlet.java          # Summary, confirm, confirmation receipt
│   │   │   │   ├── CustomerDashboardServlet.java# Customer overview & stats
│   │   │   │   ├── CustomerBookingsServlet.java # My Bookings & cancellation
│   │   │   │   ├── CustomerProfileServlet.java  # Customer profile view
│   │   │   │   ├── AdminDashboardServlet.java   # Admin KPI metrics & recent table
│   │   │   │   ├── AdminRoomServlet.java        # Admin room inventory CRUD
│   │   │   │   ├── AdminBookingServlet.java     # Admin reservation manager & status
│   │   │   │   └── AdminCustomerServlet.java    # Admin customer directory
│   │   │   └── util/
│   │   │       ├── CsrfUtil.java                # CSRF token generator & validator
│   │   │       ├── CurrencyUtil.java            # Currency presentation formatter
│   │   │       ├── DateUtil.java                # ISO date parser, night & overlap math
│   │   │       ├── PasswordUtil.java            # BCrypt hashing & verification
│   │   │       └── ValidationUtil.java          # RFC email, phone & XSS sanitization
│   │   ├── resources/
│   │   │   └── application.properties           # Fallback configuration properties
│   │   └── webapp/
│   │       ├── assets/
│   │       │   ├── css/
│   │       │   │   ├── style.css                # Luxury hospitality design system
│   │       │   │   └── admin.css                # Admin portal styling & sidebar
│   │       │   ├── js/
│   │       │   │   ├── main.js                  # Navigation, alerts, registration validator
│   │       │   │   ├── booking.js               # Dynamic night & subtotal calculator
│   │       │   │   └── admin.js                 # Admin confirmation dialogs
│   │       │   └── images/
│   │       │       ├── logo.svg                 # Vector hotel brand emblem
│   │       │       ├── hero-hotel.svg           # High-resolution architectural banner
│   │       │       ├── room-suite-1.svg         # Presidential suite illustration
│   │       │       ├── room-suite-2.svg         # Executive penthouse illustration
│   │       │       ├── room-deluxe-1.svg        # Ocean breeze illustration
│   │       │       ├── room-deluxe-2.svg        # Garden poolside illustration
│   │       │       ├── room-family-1.svg        # Family garden villa illustration
│   │       │       ├── room-family-2.svg        # Coastal family retreat illustration
│   │       │       ├── room-standard-1.svg      # Classic king comfort illustration
│   │       │       └── room-standard-2.svg      # Classic twin urban illustration
│   │       └── WEB-INF/
│   │           ├── web.xml                      # Servlet 4.0 deployment descriptor
│   │           └── views/
│   │               ├── common/                  # header, footer, navbar, messages, admin-sidebar
│   │               ├── home.jsp                 # Landing page & featured showcase
│   │               ├── auth/                    # login.jsp, register.jsp
│   │               ├── rooms/                   # room-list.jsp, room-details.jsp
│   │               ├── bookings/                # booking-summary.jsp, booking-confirmation.jsp
│   │               ├── customer/                # dashboard.jsp, my-bookings.jsp, booking-details.jsp, profile.jsp
│   │               ├── admin/                   # dashboard.jsp, rooms.jsp, room-form.jsp, bookings.jsp, booking-details.jsp, customers.jsp
│   │               └── error/                   # 403.jsp, 404.jsp, 500.jsp, general.jsp
│   └── test/java/com/hotelreservation/
│       ├── DateUtilTest.java                    # ISO dates & overlap matrix tests
│       ├── BookingCostBreakdownTest.java        # BigDecimal financial calculations tests
│       ├── PasswordUtilTest.java                # BCrypt hash & unique salt tests
│       ├── ValidationUtilTest.java              # Email, phone & XSS tests
│       ├── RoleAndStatusTransitionTest.java     # RBAC & state transition tests
│       └── BookingServiceLogicTest.java         # Overlap & concurrency logic tests
└── target/
    └── hotel-reservation-system.war             # Packaged, deployable WAR archive
```

---

## 9. Prerequisites & Environment Setup

### Required Software
1. **JDK 17 LTS or higher**: Verify with `java -version` and `javac -version`.
2. **MongoDB Community Server (v6.0 - v8.x)**: Default port `27017`.
3. **MongoDB Compass**: Official visual GUI.
4. **Apache Tomcat 9.0.x**: Standard servlet container supporting `javax.servlet.*`.
5. **Apache Maven 3.8+** (Optional: the provided `build.bat` can build directly using JDK).

### Configuring Environment Variables
The application reads configuration from environment variables first, falling back to `src/main/resources/application.properties`.

Create a `.env` file or export variables:
```bash
# MongoDB Connection String (Local default)
MONGODB_URI=mongodb://localhost:27017

# MongoDB Database Name
MONGODB_DATABASE=hotel_reservation_db

# Initial Admin Bootstrap Account
ADMIN_BOOTSTRAP_EMAIL=admin@hotelreservation.com
ADMIN_BOOTSTRAP_PASSWORD=Admin@12345
ADMIN_BOOTSTRAP_NAME=Hotel General Manager
ADMIN_BOOTSTRAP_PHONE=+1-555-019-9000

# Hospitality Tax Rate (%)
HOTEL_TAX_PERCENTAGE=12.0
```

---

## 10. Build, Test, and Deployment Guide

### Step 1: Start MongoDB
Ensure MongoDB is running locally:
```powershell
# Either start via Windows Service:
net start MongoDB

# Or run the included helper batch script:
.\run-local-mongo.bat
```
Verify connectivity in MongoDB Compass by connecting to `mongodb://localhost:27017`.

### Step 2: Compile & Run Automated Tests
Execute the test suite (28 comprehensive unit & mock tests):
```powershell
# Using Maven:
mvn test

# Or using the included Windows batch runner:
.\test.bat
```
Expected output:
```
OK (28 tests)
```

### Step 3: Package the WAR Archive
Build the WAR archive:
```powershell
# Using Maven:
mvn clean package

# Or using the included Windows build script:
.\build.bat
```
The deployable file is generated at:
```
target/hotel-reservation-system.war
```

### Step 4: Deploy to Apache Tomcat 9
1. Copy `target/hotel-reservation-system.war` to your Tomcat `webapps/` folder:
   ```powershell
   copy target\hotel-reservation-system.war C:\apache-tomcat-9.0.xx\webapps\
   ```
2. Start Tomcat:
   ```powershell
   C:\apache-tomcat-9.0.xx\bin\startup.bat
   ```
3. Open your browser and navigate to:
   ```
   http://localhost:8080/hotel-reservation-system/
   ```
4. On startup, `AppContextListener` will automatically connect to MongoDB, verify indexes, seed the 8 sample rooms, and create the bootstrap administrator account.

---

## 11. Default Credentials & Seed Data

### Bootstrap Administrator Account
- **Email**: `admin@hotelreservation.com`
- **Password**: `Admin@12345`
- **Role**: `ADMIN`
- **Dashboard URL**: `/admin/dashboard`

### Auto-Seeded Room Inventory
When initialized against an empty database, 8 realistic sample rooms are seeded automatically:
1. **Room 101**: Presidential Ocean View Suite ($495.00/night, Capacity: 4, Suite)
2. **Room 102**: Executive Penthouse Suite ($380.00/night, Capacity: 3, Suite)
3. **Room 201**: Deluxe Ocean Breeze King ($260.00/night, Capacity: 3, Deluxe)
4. **Room 202**: Deluxe Garden & Pool Access ($225.00/night, Capacity: 4, Deluxe)
5. **Room 301**: Grand Family Haven Villa ($340.00/night, Capacity: 6, Family)
6. **Room 302**: Coastal Family Retreat ($290.00/night, Capacity: 5, Family)
7. **Room 401**: Classic King Comfort ($150.00/night, Capacity: 2, Standard)
8. **Room 402**: Classic Twin Urban ($135.00/night, Capacity: 2, Standard)

---

## 12. Automated & Manual Integration Testing

### Automated Test Matrix
Run `.\test.bat` or `mvn test` to execute:
- `DateUtilTest`: Format verification, boundary checking, night calculation, and overlap detection across 7 scenarios.
- `BookingCostBreakdownTest`: Precision math tests verifying `BigDecimal` rounding and zero-loss financial accounting.
- `PasswordUtilTest`: BCrypt hash uniqueness, matching verification, and invalid password rejection.
- `ValidationUtilTest`: RFC email checks, case normalization, phone regex, and HTML XSS sanitization.
- `RoleAndStatusTransitionTest`: RBAC resolution, occupancy flag checking, and lifecycle transitions.
- `BookingServiceLogicTest`: Past date rejection, invalid checkout rejection, capacity violations, concurrent conflict rejection, valid booking creation, and customer ownership security checks.

### Manual End-to-End Checklist
1. **Registration**: Register a new user at `/register`. Verify the record appears in Compass with hashed password.
2. **Duplicate Email Prevention**: Attempt to register again with the same email. Verify duplicate rejection.
3. **Sign In & Session**: Sign in at `/login`. Verify session is established and header reflects user name.
4. **Room Search**: Filter by dates and guest count = 4. Verify only matching rooms appear.
5. **Booking Creation**: Select Room 101. Choose dates. Review summary and confirm booking. Verify confirmation number is issued.
6. **Double Booking Attempt**: Try booking the exact same room for the same dates. Verify the system blocks the request.
7. **Cancellation**: In "My Reservations", cancel the booking. Verify status updates to `CANCELLED` and room becomes available again.
8. **Admin Operations**: Log in as `admin@hotelreservation.com`. Access `/admin/dashboard`. Verify live KPI metrics, add a room, update status from `CONFIRMED` to `CHECKED_IN`, and verify customer directory.

---

## 13. Viva Voce & Technical Defense Preparation

### Sample Questions & Answers for Examination

**Q1: Why did you choose MongoDB instead of a traditional relational database (RDBMS)?**  
*Answer:* MongoDB’s flexible, schemaless BSON document model allows storing complex room properties (like an array of amenities) directly in a single document without requiring join tables. It provides native indexing, scalable horizontal sharding, high write throughput, and seamless JSON/BSON mapping to Java objects.

**Q2: How do you prevent financial rounding errors in monetary transactions?**  
*Answer:* We avoid primitive `double` or `float` due to binary floating-point imprecision. In Java, all price calculations use `java.math.BigDecimal` with `RoundingMode.HALF_UP`. In MongoDB, amounts are stored as `Decimal128` (IEEE 754-2008 decimal floating-point format).

**Q3: Explain the mathematical overlap condition used to prevent double booking.**  
*Answer:* Two stay intervals $[In_1, Out_1)$ and $[In_2, Out_2)$ overlap if and only if $In_1 < Out_2$ and $Out_1 > In_2$. This condition is checked only against active bookings (`CONFIRMED` and `CHECKED_IN`). It permits consecutive reservations where one guest checks out at 11:00 AM and another arrives at 3:00 PM on the same date.

**Q4: How does the application handle concurrent booking attempts for the same room?**  
*Answer:* In our service layer, we maintain a per-room `ReentrantLock` in a `ConcurrentHashMap`. When confirming a reservation, the room lock is acquired, and availability is re-verified inside the critical section before inserting the booking document. In a distributed multi-instance environment, this would be extended to MongoDB multi-document ACID transactions or distributed locks (e.g., Redis Redlock).

**Q5: How are passwords stored and authenticated securely?**  
*Answer:* Passwords are never stored in plaintext. We utilize **BCrypt** (`jbcrypt`) with a work factor of 12 rounds. BCrypt automatically generates a cryptographically secure 128-bit salt and hashes the password using the Blowfish cipher. Passwords are never returned to JSP views or logs.

**Q6: What is the purpose of the ServletContextListener?**  
*Answer:* `AppContextListener` hooks into the web application lifecycle. On startup (`contextInitialized`), it initializes a single shared `MongoClient`, verifies collections and indexes, and seeds bootstrap data. On shutdown (`contextDestroyed`), it gracefully closes the client, releasing connection pool sockets.

**Q7: What is CSRF and how does the application defend against it?**  
*Answer:* Cross-Site Request Forgery occurs when a malicious site tricks a user's browser into executing unauthorized state-changing requests on a site where they are authenticated. We mitigate this using a `CsrfFilter` that generates a secure, random token stored in the session. All state-changing POST/PUT/DELETE forms must submit a matching `_csrf` token parameter.

**Q8: Why is Session Fixation dangerous and how is it mitigated?**  
*Answer:* Session fixation occurs when an attacker sets a victim's session ID before they log in. We protect against this by invalidating any existing session upon successful credential verification and issuing a fresh session (`request.getSession(true)`), regenerating the JSESSIONID cookie.

**Q9: What is the Post/Redirect/Get (PRG) pattern?**  
*Answer:* When a customer confirms a reservation via POST, rather than directly rendering the confirmation view in response to the POST request, the server responds with an HTTP 302 redirect to `GET /booking/confirmation?num=...`. This prevents duplicate form submissions if the user refreshes their browser.

**Q10: Why did you use `javax.servlet.*` instead of `jakarta.servlet.*`?**  
*Answer:* Apache Tomcat 9 implements the Java Servlet 4.0 specification, which uses the `javax.servlet` namespace. Tomcat 10+ transitioned to the Jakarta EE `jakarta.servlet` namespace. To maintain full compatibility with Tomcat 9, `javax.servlet:javax.servlet-api:4.0.1` is used consistently throughout.

