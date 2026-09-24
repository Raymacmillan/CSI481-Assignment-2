package csi481.util;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Hardcoded booster-interval business rule.
 *
 * IMPORTANT (flagged deliberately, not hidden): the model solution diagram
 * for this assignment has no ScheduleEntry-style class recording the
 * standard immunisation timetable, unlike the Part 1 conceptual design.
 * Vaccination.BoosterNo is a plain integer with no attached recommended
 * age or minimum interval anywhere in the database. Consequently, the
 * "recommended interval before the next booster" cannot be looked up from
 * data at all, and has to live here, as a hardcoded constant per vaccine
 * name, exactly as flagged when the diagram was first read.
 *
 * This is a genuine structural limitation of the given diagram, not an
 * implementation shortcut, and should be stated as such in the logical.pdf
 * writeup and be ready to defend in a viva.
 */
public final class BoosterScheduler {

    // Interval, in days, before the NEXT booster is due, keyed by vaccine
    // name (case-insensitive). Falls back to DEFAULT_INTERVAL_DAYS when a
    // vaccine name has no specific entry, so the system never fails to
    // propose a date, it simply falls back to a generic assumption.
    private static final Map<String, Integer> INTERVAL_DAYS_BY_VACCINE = new HashMap<>();
    private static final int DEFAULT_INTERVAL_DAYS = 28; // 4 weeks, a common EPI spacing

    static {
        INTERVAL_DAYS_BY_VACCINE.put("bcg", 0);              // single dose, no booster
        INTERVAL_DAYS_BY_VACCINE.put("dtp", 28);             // 4 weeks between DTP doses
        INTERVAL_DAYS_BY_VACCINE.put("pentavalent", 28);     // 4 weeks between doses
        INTERVAL_DAYS_BY_VACCINE.put("polio", 28);           // 4 weeks between doses
        INTERVAL_DAYS_BY_VACCINE.put("measles-rubella", 270); // roughly 9 to 18 months
        INTERVAL_DAYS_BY_VACCINE.put("hepatitis b", 28);
    }

    private BoosterScheduler() {
    }

    public static int intervalDaysFor(String vaccineName) {
        if (vaccineName == null) {
            return DEFAULT_INTERVAL_DAYS;
        }
        return INTERVAL_DAYS_BY_VACCINE.getOrDefault(vaccineName.trim().toLowerCase(), DEFAULT_INTERVAL_DAYS);
    }

    public static LocalDate proposeNextBoosterDate(LocalDate lastVaccinationDate, String vaccineName) {
        LocalDate base = (lastVaccinationDate != null) ? lastVaccinationDate : LocalDate.now();
        return base.plusDays(intervalDaysFor(vaccineName));
    }
}
