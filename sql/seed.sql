-- CSI481 Assignment 2: test data.
-- Enough rows in every relation to demonstrate every GUI screen and every
-- required report without the marker having to enter data first.
-- Dates are chosen relative to the assignment period (September/October
-- 2026) so the six-month audit and the weekly work-list both return
-- non-empty results out of the box.

USE csi481_immunisation;

-- ---------------------------------------------------------------- District
INSERT INTO District (Name) VALUES
    ('Gaborone'),
    ('Francistown'),
    ('Kweneng');

-- ----------------------------------------------------------------- Practice
INSERT INTO Practice (Location, DistrictID) VALUES
    ('Gaborone West Clinic', 1),
    ('Broadhurst Health Post', 1),
    ('Francistown Central Clinic', 2),
    ('Molepolole Clinic', 3);

-- ----------------------------------------------------------------------- GP
INSERT INTO GP (GPNo, Name, PracticeID) VALUES
    (1001, 'Kagiso Modise', 1),
    (1002, 'Thabo Ntsima', 1),
    (1003, 'Lesego Phiri', 2),
    (1004, 'Naledi Kgosi', 3),
    (1005, 'Otsile Ramotswe', 4);

-- ------------------------------------------------------------------ Guardian
INSERT INTO Guardian (ONo, Name) VALUES
    (501234567, 'Boitumelo Sethunya'),
    (502345678, 'Kabo Mokgatle'),
    (503456789, 'Refilwe Tau'),
    (504567890, 'Mpho Dikgang'),
    (505678901, 'Tshegofatso Baruti');

-- ------------------------------------------------------------------- Patient
INSERT INTO Patient (BirthCertNo, Name, DateOfBirth, ONo, GPNo) VALUES
    (900001, 'Katlego Sethunya',  '2026-01-15', 501234567, 1001),
    (900002, 'Amantle Mokgatle',  '2026-02-20', 502345678, 1001),
    (900003, 'Tumelo Tau',        '2026-03-05', 503456789, 1002),
    (900004, 'Lorato Dikgang',    '2026-04-18', 504567890, 1003),
    (900005, 'Neo Baruti',        '2026-05-30', 505678901, 1004),
    (900006, 'Wame Sethunya',     '2026-06-10', 501234567, 1005);

-- ------------------------------------------------------------------- Vaccine
INSERT INTO Vaccine (Name, Manufacturer, Type) VALUES
    ('BCG', 'Serum Institute of India', 'Tuberculosis'),
    ('Pentavalent', 'GSK', 'Diphtheria/Pertussis/Tetanus/HepB/Hib'),
    ('Polio (OPV)', 'Sanofi', 'Poliomyelitis'),
    ('Measles-Rubella', 'Serum Institute of India', 'Measles/Rubella'),
    ('Hepatitis B', 'GSK', 'Hepatitis B');

-- --------------------------------------------------------------- Vaccination
-- Administered doses, spread across the last six months so the audit
-- report has real numbers to group and count.
INSERT INTO Vaccination (DateOfVac, BoosterNo, BatchNo, BirthCertNo, GPNo, VaccineID) VALUES
    ('2026-01-16', 1, 'BCG-2601-A', 900001, 1001, 1),
    ('2026-02-13', 1, 'PENTA-2602-A', 900001, 1001, 2),
    ('2026-03-13', 2, 'PENTA-2603-A', 900001, 1001, 2),
    ('2026-02-21', 1, 'BCG-2602-B', 900002, 1001, 1),
    ('2026-03-20', 1, 'PENTA-2603-B', 900002, 1001, 2),
    ('2026-03-06', 1, 'BCG-2603-C', 900003, 1002, 1),
    ('2026-04-03', 1, 'PENTA-2604-A', 900003, 1002, 2),
    ('2026-05-01', 2, 'PENTA-2605-A', 900003, 1002, 2),
    ('2026-04-19', 1, 'BCG-2604-D', 900004, 1003, 1),
    ('2026-07-15', 1, 'OPV-2607-A', 900004, 1003, 3),
    ('2026-05-31', 1, 'BCG-2605-E', 900005, 1004, 1),
    ('2026-08-01', 1, 'HEPB-2608-A', 900005, 1004, 5),
    ('2026-06-11', 1, 'BCG-2606-F', 900006, 1005, 1),
    ('2026-09-01', 1, 'MR-2609-A', 900006, 1005, 4);

-- --------------------------------------------------------------- Appointment
-- A mix of SCHEDULED appointments: some in the coming week (for the
-- work-list demo), one further out, and one already CANCELLED to show the
-- status column is meaningful, not decorative.
INSERT INTO Appointment (ScheduledDate, BoosterNo, BirthCertNo, GPNo, VaccineID, Status) VALUES
    ('2026-09-28', 3, 900001, 1001, 2, 'SCHEDULED'),  -- Pentavalent booster 3 for Katlego
    ('2026-09-30', 2, 900002, 1001, 2, 'SCHEDULED'),  -- Pentavalent booster 2 for Amantle
    ('2026-10-02', 3, 900003, 1002, 2, 'SCHEDULED'),  -- Pentavalent booster 3 for Tumelo
    ('2026-10-15', 2, 900004, 1003, 3, 'SCHEDULED'),  -- Polio booster 2 for Lorato
    ('2026-09-25', 2, 900005, 1004, 5, 'CANCELLED');  -- Cancelled Hep B booster for Neo
