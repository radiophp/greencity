package greencity;

import java.util.Objects;

/**
 * Immutable POJO representing an energy‑related device.
 */
public record Device(
        String name,
        String category,
        double costTRY,
        double energySavedKWh,
        int sustainabilityScore
) {
    /** Convenience “value” used by the objective: energy × score. */
    public double objectiveValue() {
        return energySavedKWh * sustainabilityScore;
    }

    @Override
    public String toString() {
        return "%s (₺%.2f, %.1f kWh/yr, score %d)".formatted(
                name, costTRY, energySavedKWh, sustainabilityScore);
    }
}