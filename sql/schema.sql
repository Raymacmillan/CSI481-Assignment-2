-- CSI481 Assignment 2: Infant Immunisation Recording System
-- Logical schema, derived from the model solution UML diagram.
-- See logical.pdf for the full derivation and justification of every
-- design decision made below (surrogate keys, NOT NULL placement, and the
-- Appointment table added beyond the diagram).

DROP DATABASE IF EXISTS csi481_immunisation;
CREATE DATABASE csi481_immunisation
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE csi481_immunisation;

-- ---------------------------------------------------------------- District
-- Diagram gives District only a Name attribute and marks no identifier,
-- so a surrogate key is introduced (logical.pdf, Section 2.1).
CREATE TABLE District (
    DistrictID  INT AUTO_INCREMENT PRIMARY KEY,
    Name        VARCHAR(100) NOT NULL,
    CONSTRAINT uq_district_name UNIQUE (Name)
) ENGINE = InnoDB;

-- ----------------------------------------------------------------- Practice
-- Diagram gives Practice only a Location attribute and marks no
-- identifier, so a surrogate key is introduced. District -> Practice is
-- 1..* / 1 on the diagram, i.e. every practice belongs to exactly one
-- district: DistrictID is therefore NOT NULL.
CREATE TABLE Practice (
    PracticeID  INT AUTO_INCREMENT PRIMARY KEY,
    Location    VARCHAR(150) NOT NULL,
    DistrictID  INT NOT NULL,
    CONSTRAINT fk_practice_district FOREIGN KEY (DistrictID)
        REFERENCES District (DistrictID)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB;

-- ----------------------------------------------------------------------- GP
-- GPNo is the natural identifier given directly in the scenario and the
-- diagram. Practice -> GP is 1..* / 1: every GP works for exactly one
-- practice, so PracticeID is NOT NULL.
-- NOTE: /NoOfPatients is a DERIVED attribute in the diagram and is
-- deliberately NOT a column here -- see GPDAO.findAll(), which computes it
-- with COUNT(*) over Patient, per constraint C5 in the Part 1 report.
CREATE TABLE GP (
    GPNo        INT PRIMARY KEY,
    Name        VARCHAR(100) NOT NULL,
    PracticeID  INT NOT NULL,
    CONSTRAINT fk_gp_practice FOREIGN KEY (PracticeID)
        REFERENCES Practice (PracticeID)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB;

-- ------------------------------------------------------------------ Guardian
-- ONo (Omang number) is the natural identifier, per the scenario and
-- diagram.
CREATE TABLE Guardian (
    ONo         INT PRIMARY KEY,
    Name        VARCHAR(100) NOT NULL
) ENGINE = InnoDB;

-- ------------------------------------------------------------------- Patient
-- BirthCertNo is the natural identifier, per R2 of the scenario and the
-- diagram. Every patient has exactly one guardian and is on the list of
-- exactly one GP (both 1..* / 1 on the diagram), so both foreign keys are
-- NOT NULL.
CREATE TABLE Patient (
    BirthCertNo INT PRIMARY KEY,
    Name        VARCHAR(100) NOT NULL,
    DateOfBirth DATE NOT NULL,
    ONo         INT NOT NULL,
    GPNo        INT NOT NULL,
    CONSTRAINT fk_patient_guardian FOREIGN KEY (ONo)
        REFERENCES Guardian (ONo)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_patient_gp FOREIGN KEY (GPNo)
        REFERENCES GP (GPNo)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB;

-- ------------------------------------------------------------------- Vaccine
-- Diagram marks no identifying attribute, so a surrogate key is
-- introduced.
CREATE TABLE Vaccine (
    VaccineID    INT AUTO_INCREMENT PRIMARY KEY,
    Name         VARCHAR(100) NOT NULL,
    Manufacturer VARCHAR(100),
    Type         VARCHAR(50)
) ENGINE = InnoDB;

-- --------------------------------------------------------------- Vaccination
-- Represents a dose ALREADY GIVEN. Corresponds exactly to the Vaccination
-- class on the diagram: DateOfVac, BoosterNo, BatchNo, with mandatory
-- (1) links to Patient, GP (the administering doctor) and Vaccine, so all
-- three foreign keys are NOT NULL.
CREATE TABLE Vaccination (
    VaccinationID INT AUTO_INCREMENT PRIMARY KEY,
    DateOfVac     DATE NOT NULL,
    BoosterNo     INT NOT NULL DEFAULT 1,
    BatchNo       VARCHAR(50) NOT NULL,
    BirthCertNo   INT NOT NULL,
    GPNo          INT NOT NULL,
    VaccineID     INT NOT NULL,
    CONSTRAINT fk_vaccination_patient FOREIGN KEY (BirthCertNo)
        REFERENCES Patient (BirthCertNo)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_vaccination_gp FOREIGN KEY (GPNo)
        REFERENCES GP (GPNo)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_vaccination_vaccine FOREIGN KEY (VaccineID)
        REFERENCES Vaccine (VaccineID)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    -- C2: at most one administered dose per patient/vaccine/booster number.
    CONSTRAINT uq_vaccination_dose UNIQUE (BirthCertNo, VaccineID, BoosterNo),
    CONSTRAINT chk_booster_positive CHECK (BoosterNo >= 1)
) ENGINE = InnoDB;

-- --------------------------------------------------------------- Appointment
-- NOT present on the model solution diagram. Added because GUI task 3
-- ("schedule the next booster... and appoint with a GP for the scheduled
-- vaccination") and two of the three required reports (the weekly
-- work-list, and appointment letters) all operate on a SCHEDULED, future
-- vaccination, which the diagram's Vaccination class cannot represent
-- since it only carries a date for a dose already given. See logical.pdf,
-- Section 4, for the full justification.
CREATE TABLE Appointment (
    AppointmentID   INT AUTO_INCREMENT PRIMARY KEY,
    ScheduledDate   DATE NOT NULL,
    BoosterNo       INT NOT NULL DEFAULT 1,
    BirthCertNo     INT NOT NULL,
    GPNo            INT NOT NULL,
    VaccineID       INT NOT NULL,
    Status          ENUM('SCHEDULED', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'SCHEDULED',
    CONSTRAINT fk_appointment_patient FOREIGN KEY (BirthCertNo)
        REFERENCES Patient (BirthCertNo)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_appointment_gp FOREIGN KEY (GPNo)
        REFERENCES GP (GPNo)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_appointment_vaccine FOREIGN KEY (VaccineID)
        REFERENCES Vaccine (VaccineID)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_appt_booster_positive CHECK (BoosterNo >= 1)
) ENGINE = InnoDB;

-- Indexes to support the reporting queries directly (work-list filters on
-- practice + date range via GP; audit filters on date range via Vaccine).
CREATE INDEX idx_vaccination_date ON Vaccination (DateOfVac);
CREATE INDEX idx_appointment_date_status ON Appointment (ScheduledDate, Status);
CREATE INDEX idx_gp_practice ON GP (PracticeID);
CREATE INDEX idx_patient_gp ON Patient (GPNo);
