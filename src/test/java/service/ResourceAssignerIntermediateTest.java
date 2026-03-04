package service;

import org.alt.bo.dto.LabInputIntermediate;
import org.alt.bo.dto.LabOutputIntermediate;
import org.alt.bo.input.Priority;
import org.alt.bo.input.intermediate.SpecialityIntermediate;
import org.alt.service.LabPlannerIntermediate;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static utils.TestUtils.*;

public class ResourceAssignerIntermediateTest {

    private final LabPlannerIntermediate planner = new LabPlannerIntermediate();

    @Test
    void analysis_should_not_start_during_lunch_window() {
        LabInputIntermediate in = input(
                List.of(sample("S1", Priority.URGENT, "Hémogramme", 30, "12:30")),
                List.of(technician("T1", List.of(SpecialityIntermediate.BLOOD), 1.0, "08:00", "17:00")),
                List.of(equipment("E1", SpecialityIntermediate.BLOOD, List.of("Hémogramme"), 10))
        );

        LabOutputIntermediate out = planner.planifyLab(in);

        LocalTime start = out.getSchedule().get(0).getStartTime();
        assertFalse(start.isBefore(LocalTime.of(13, 30)),
                "L'analyse ne devrait pas démarrer pendant la pause déjeuner");
    }

    @Test
    void analysis_before_lunch_window_should_not_be_delayed() {
        LabInputIntermediate in = input(
                List.of(sample("S1", Priority.URGENT, "Hémogramme", 30, "10:00")),
                List.of(technician("T1", List.of(SpecialityIntermediate.BLOOD), 1.0, "08:00", "17:00")),
                List.of(equipment("E1", SpecialityIntermediate.BLOOD, List.of("Hémogramme"), 10))
        );

        LabOutputIntermediate out = planner.planifyLab(in);

        assertEquals(LocalTime.parse("10:00"), out.getSchedule().get(0).getStartTime());
    }
}
