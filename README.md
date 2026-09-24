# CSI481 Assignment 2 — Infant Immunisation Recording System

## What gets submitted

Exactly **two files**, nothing else:

1. `dist/InfantImmunisationSystem.jar` — the application. It is a **self-contained ("fat") JAR**: the MySQL/MariaDB Connector/J driver is bundled inside it, so it runs on its own without any other JAR next to it.
2. `logical.pdf` — the logical design document (fill in the group's names/IDs on page 1 first).

## What's in this repository

- `dist/InfantImmunisationSystem.jar` — the self-contained application JAR (the submission file).
- `dist/mysql-connector-j-9.7.0.jar` — the JDBC driver. Only needed when **rebuilding** the fat JAR; it is not needed to run the app.
- `logical.pdf` / `logical.docx` — the logical design document.
- `sql/schema.sql` — creates the `csi481_immunisation` database and all seven tables with constraints.
- `sql/seed.sql` — test data for every table (run after `schema.sql`).
- `src/` — full Java source.
- `manifest.txt` — JAR manifest (`Main-Class` only; no `Class-Path`, since the driver is bundled).

## Setup and running

1. Load the schema and test data into MariaDB:
   ```bash
   sudo mariadb -u root -p < sql/schema.sql
   sudo mariadb -u root -p < sql/seed.sql
   ```

2. The database credentials are **hard-coded** (as the assignment requires) in `src/csi481/db/DBConnection.java`:
   ```java
   private static final String USER = "csi481app";
   private static final String PASSWORD = "Csi481Pass!";
   ```
   Make sure a MariaDB user with these credentials exists and has access to `csi481_immunisation`, or change the values to match your server and rebuild (step 3).

3. Rebuild the self-contained JAR (only needed after changing the source):
   ```bash
   find src -name "*.java" > sources.txt
   rm -rf build_fat && mkdir build_fat
   javac -d build_fat -cp src @sources.txt
   (cd build_fat && jar xf ../dist/mysql-connector-j-9.7.0.jar)
   rm -f build_fat/META-INF/MANIFEST.MF
   jar --create --file dist/InfantImmunisationSystem.jar --manifest manifest.txt -C build_fat .
   ```
   This compiles the application, unpacks the Connector/J driver into the same build folder, and packages both into one JAR.

4. Run it — from **any** folder, with nothing else alongside it:
   ```bash
   java -jar InfantImmunisationSystem.jar
   ```

## Verifying the submission JAR

The real test is running the JAR from a folder that contains **only** that one file — exactly what the lecturer will have:

```bash
mkdir -p /tmp/jartest
cp dist/InfantImmunisationSystem.jar /tmp/jartest/
cd /tmp/jartest
java -jar InfantImmunisationSystem.jar
```

If the main window ("CSI481 -- Infant Immunisation Recording System") opens, the driver is bundled correctly and the hard-coded credentials connect. If a "Database Connection Failed" dialog appears instead, MariaDB is not running or the credentials in `DBConnection.java` don't match the server. Once it opens, work through each of the five sidebar screens against the seeded test data.

## What the five sidebar screens map to in the assignment brief

| Sidebar screen | Assignment requirement |
|---|---|
| 1. Add Records | Add a new GP, vaccine, patient and parent |
| 2. Register Vaccination | Register a vaccination against existing vaccine/patient/GP |
| 3. Schedule Booster | Schedule the next booster and appoint a GP |
| 4. Patient List | List of patients and their vaccination details |
| 5. Reports | Appointment letters, weekly work-list, six-month audit |

## One deliberate deviation from the model solution diagram

The diagram has no entity representing a *scheduled, not-yet-given* vaccination. An `Appointment` table was added beyond the diagram to make screen 3 and two of the three required reports possible at all. This is explained in full, with reasoning, in `logical.pdf`, Section 4. Be ready to explain this choice if asked — it is a genuine, defensible gap in the supplied diagram, not an implementation shortcut.
