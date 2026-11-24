# VoltSlot EV – Smart EV Charging & ANPR Slot Booking Platform

## Overview
VoltSlot EV is a Spring Boot web application designed for electric vehicle owners to reserve fast-charging slots online using their vehicle registration plate, verify arrival via Automatic Number Plate Recognition (ANPR) 15 minutes prior to the slot, monitor real-time battery State of Charge (SoC %), and automatically enforce overstay and no-show penalties.

## Tech Stack
- **Java:** 25 (Azul Zulu)
- **Framework:** Spring Boot (Spring Web, Spring Data JPA, Thymeleaf, Bean Validation)
- **Database:** MySQL (Relational Database with UUID Primary Keys)
- **Frontend:** Thymeleaf + Bootstrap 5
- **Build Tool:** Maven

## Architecture & Domain Entities
1. **User (`users`):** Stores user credentials (SHA-256 hashed passwords) and roles (`USER`, `ADMIN`).
2. **ChargingStation (`charging_stations`):** Represents EV charging points with power categories (`AC_22KW`, `DC_FAST_150KW`, `ULTRA_FAST_350KW`), location, pricing per kWh in EUR, and availability status.
3. **ChargingSession (`charging_sessions`):** Tracks slot reservations bound to a vehicle license plate, online payment totals, battery charging progress (`%`), ANPR verification status, and incurred penalties.

## Key Features & Functionalities
1. **Session-Based Authentication & Role Access Control:**
   - Guest access restricted to Home, Rules, Login, and Register pages.
   - Authenticated access via `user_id` stored in `HttpSession` with role checks (`ADMIN` vs. `USER`).
2. **Charging Stations Full CRUD (Admin):**
   - Administrators can create, view, edit, and delete charging stations with server-side validation.
3. **License Plate Slot Booking & Online Payment:**
   - Logged-in users reserve available charging stations by entering their vehicle registration number, start time, and duration.
4. **ANPR Verification (15 Minutes Before Slot):**
   - Automatic Number Plate Recognition unlocks the charger only when the detected license plate matches the reserved vehicle, preventing unauthorized use.
5. **Battery Monitoring & Automatic 5.00 EUR Penalties:**
   - Displays live battery charging percentage (`SoC %`).
   - Automatically applies a **5.00 EUR** penalty if the vehicle fails to occupy the slot within 15 minutes of the start time, or overstays more than 15 minutes after charging completes.