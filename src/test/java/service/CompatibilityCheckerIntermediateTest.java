package service;

import org.alt.bo.dto.LabInputIntermediate;
import org.alt.bo.dto.LabOutputIntermediate;
import org.alt.bo.input.Priority;
import org.alt.bo.input.intermediate.SpecialityIntermediate;
import org.alt.service.LabPlannerIntermediate;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.TestUtils.*;

class CompatibilityCheckerIntermediateTest {

    private final LabPlannerIntermediate planner = new LabPlannerIntermediate();

    @Test
    void incompatible_technician_should_not_be_assigned() {
        LabInputIntermediate in = input(
                List.of(sample("S1", Priority.URGENT, "Hémogramme", 30, "09:00")),
                List.of(technician("T1", List.of(SpecialityIntermediate.CHEMISTRY), 1.0, "08:00", "17:00")),
                List.of(equipment("E1", SpecialityIntermediate.BLOOD, List.of("Hémogramme"), 10))
        );

        LabOutputIntermediate out = planner.planifyLab(in);

        assertEquals(1, out.getMetrics().getConflicts());
        assertTrue(out.getSchedule().isEmpty());
    }

    @Test
    void compatible_technician_should_be_assigned() {
        LabInputIntermediate in = input(
                List.of(sample("S1", Priority.URGENT, "Hémogramme", 30, "09:00")),
                List.of(
                        technician("T1", List.of(SpecialityIntermediate.CHEMISTRY), 1.0, "08:00", "17:00"),
                        technician("T2", List.of(SpecialityIntermediate.BLOOD), 1.0, "08:00", "17:00")
                ),
                List.of(equipment("E1", SpecialityIntermediate.BLOOD, List.of("Hémogramme"), 10))
        );

        LabOutputIntermediate out = planner.planifyLab(in);

        assertEquals("T2", out.getSchedule().get(0).getTechnicianId());
        assertEquals(0, out.getMetrics().getConflicts());
    }

    @Test
    void polyvalent_technician_should_handle_any_analysis() {
        LabInputIntermediate in = input(
                List.of(
                        sample("S1", Priority.URGENT, "Hémogramme", 30, "09:00"),
                        sample("S2", Priority.URGENT, "Bilan hépatique", 30, "09:00")
                ),
                List.of(technician("T1", List.of(SpecialityIntermediate.BLOOD, SpecialityIntermediate.CHEMISTRY), 1.0, "08:00", "17:00")),
                List.of(
                        equipment("E1", SpecialityIntermediate.BLOOD, List.of("Hémogramme"), 10),
                        equipment("E2", SpecialityIntermediate.CHEMISTRY, List.of("Bilan hépatique"), 15)
                )
        );

        LabOutputIntermediate out = planner.planifyLab(in);

        assertEquals(2, out.getSchedule().size());
        assertEquals(0, out.getMetrics().getConflicts());
    }
}