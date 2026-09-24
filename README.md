# CSI481 Assignment 2 — Infant Immunisation Recording System

## What's in this package

- `logical.pdf` — the required logical design document. Submit this as-is once your group's names/IDs are filled in on page 1.
- `sql/schema.sql` — creates the `csi481_immunisation` database and all seven tables with constraints.
- `sql/seed.sql` — test data for every table (run after schema.sql).
- `dist/InfantImmunisationSystem.jar` — the compiled application.
- `src/` — full Java source, in case you need to change anything (e.g. the hardcoded DB password) and rebuild.

## One-time setup on your machine

1. Load the schema and test data into MariaDB:
   ```bash
   sudo mariadb -u root -p < sql/schema.sql
   sudo mariadb -u root -p < sql/seed.sql
   ```

2. **Edit the hardcoded credentials** to match your MariaDB root password (or a dedicated app user), in `src/csi481/db/DBConnection.java`:
   ```java
   private static final String USER = "root";
   private static final String PASSWORD = "millanryo2004!";
   ```
   Change `PASSWORD` to whatever your actual MariaDB password is. This is the file the assignment's "hardcode the username/password" requirement refers to.

3. Rebuild the JAR after any change to `DBConnection.java`:
   ```bash
   cd csi481-project
   find src -name "*.java" > sources.txt
   rm -rf build && mkdir build
   javac -d build -cp src @sources.txt
   jar --create --file dist/InfantImmunisationSystem.jar --manifest manifest.txt -C build .
   ```

4. Place the MySQL/MariaDB Connector/J jar (`mysql-connector-j-9.7.0.jar`, the one you already downloaded to `~/Documents/CSI481/practice/lib/`) **in the same folder as `InfantImmunisationSystem.jar`**. The manifest's `Class-Path` entry expects it right next to the JAR, not in a `lib/` subfolder — copy it there rather than relying on a relative `lib/` path, since manifest classpaths are resolved relative to the JAR's own location and a bare filename means "same directory."

5. Run it:
   ```bash
   cd dist
   java -jar InfantImmunisationSystem.jar
   ```

## Why the compile-verification here does not equal a runtime test

This project was written and compiled inside a sandbox with no MariaDB server, no JDBC driver reachable over the network, and no display. Everything that `javac` can check has been checked: all 27 source files compile cleanly against the JDK with zero errors. Actually clicking through every screen against a live MariaDB instance and confirming inserts land correctly, dropdowns populate, and reports return the expected rows is something only you can do on your Kali machine, exactly like the JDBC connection test earlier in this project. Run it, work through each of the five sidebar screens against the seeded test data, and report back anything that doesn't behave as documented here.

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
