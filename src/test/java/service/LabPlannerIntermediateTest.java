package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.TestUtils.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalTime;
import java.util.List;
import org.alt.bo.dto.LabInputIntermediate;
import org.alt.bo.dto.LabOutputIntermediate;
import org.alt.bo.input.Priority;
import org.alt.bo.input.intermediate.SpecialityIntermediate;
import org.alt.service.LabPlannerIntermediate;
import org.alt.utils.JsonUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

class LabPlannerIntermediateTest {

  private final ObjectMapper mapper = JsonUtils.mapper();
  private final LabPlannerIntermediate planner = new LabPlannerIntermediate();

  @ParameterizedTest
  @ValueSource(strings = {"example4", "example5"})
  void examples_should_match_expected_output(String name) throws Exception {

    LabInputIntermediate input = JsonUtils.read(name + "-input.json", LabInputIntermediate.class);
    LabOutputIntermediate actualOutput = planner.planifyLab(input);

    String expected = mapper.writeValueAsString(JsonUtils.readAsJsonNode(name + "-expected.json"));
    String actual = mapper.writeValueAsString(mapper.valueToTree(actualOutput));

    JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
  }

  @Test
  void single_sample_should_be_scheduled() {
    LabInputIntermediate input =
        input(
            List.of(sample("S1", Priority.URGENT, "Hémogramme", 30, "09:00")),
            List.of(technician("T1", List.of(SpecialityIntermediate.BLOOD), 1.0, "08:00", "17:00")),
            List.of(equipment("E1", SpecialityIntermediate.BLOOD, List.of("Hémogramme"), 10)));

    LabOutputIntermediate output = planner.planifyLab(input);

    assertEquals(1, output.getSchedule().size());
    assertEquals(0, output.getMetrics().getConflicts());
    assertEquals("S1", output.getSchedule().get(0).getSampleId());
  }

  @Test
  void sample_should_start_at_arrival_time() {
    LabInputIntermediate input =
        input(
            List.of(sample("S1", Priority.URGENT, "Hémogramme", 30, "09:00")),
            List.of(technician("T1", List.of(SpecialityIntermediate.BLOOD), 1.0, "08:00", "17:00")),
            List.of(equipment("E1", SpecialityIntermediate.BLOOD, List.of("Hémogramme"), 10)));

    LabOutputIntermediate output = planner.planifyLab(input);

    assertEquals(LocalTime.parse("09:00"), output.getSchedule().get(0).getStartTime());
    assertEquals(LocalTime.parse("09:30"), output.getSchedule().get(0).getEndTime());
  }

  @Test
  void sample_arriving_before_technician_shift_should_wait() {
    LabInputIntermediate input =
        input(
            List.of(sample("S1", Priority.URGENT, "Hémogramme", 30, "07:00")),
            List.of(technician("T1", List.of(SpecialityIntermediate.BLOOD), 1.0, "09:00", "17:00")),
            List.of(equipment("E1", SpecialityIntermediate.BLOOD, List.of("Hémogramme"), 10)));

    LabOutputIntermediate output = planner.planifyLab(input);

    assertEquals(LocalTime.parse("09:00"), output.getSchedule().get(0).getStartTime());
  }

  @Test
  void no_technician_should_produce_conflict() {
    LabInputIntermediate input =
        input(
            List.of(sample("S1", Priority.URGENT, "Hémogramme", 30, "09:00")),
            List.of(),
            List.of(equipment("E1", SpecialityIntermediate.BLOOD, List.of("Hémogramme"), 10)));

    LabOutputIntermediate output = planner.planifyLab(input);

    assertTrue(output.getSchedule().isEmpty());
  }

  @Test
  void expert_technician_should_finish_faster() {
    // 45 / 1.2 = 37.5 → Math.round = 38min
    LabInputIntermediate in =
        input(
            List.of(sample("S1", Priority.URGENT, "Hémogramme", 45, "09:00")),
            List.of(technician("T1", List.of(SpecialityIntermediate.BLOOD), 1.2, "08:00", "17:00")),
            List.of(equipment("E1", SpecialityIntermediate.BLOOD, List.of("Hémogramme"), 10)));

    LabOutputIntermediate out = planner.planifyLab(in);

    assertEquals(38, out.getSchedule().get(0).getDuration());
    assertEquals(LocalTime.parse("09:38"), out.getSchedule().get(0).getEndTime());
  }

  @Test
  void junior_technician_should_take_longer() {
    // 30 / 0.9 = 33.3 → Math.round = 33min
    LabInputIntermediate in =
        input(
            List.of(sample("S1", Priority.URGENT, "Hémogramme", 30, "09:00")),
            List.of(technician("T1", List.of(SpecialityIntermediate.BLOOD), 0.9, "08:00", "17:00")),
            List.of(equipment("E1", SpecialityIntermediate.BLOOD, List.of("Hémogramme"), 10)));

    LabOutputIntermediate out = planner.planifyLab(in);

    assertEquals(33, out.getSchedule().get(0).getDuration());
    assertEquals(LocalTime.parse("09:33"), out.getSchedule().get(0).getEndTime());
  }

  @Test
  void standard_efficiency_should_not_change_duration() {
    // 30 / 1.0 = 30min exactement
    LabInputIntermediate in =
        input(
            List.of(sample("S1", Priority.URGENT, "Hémogramme", 30, "09:00")),
            List.of(technician("T1", List.of(SpecialityIntermediate.BLOOD), 1.0, "08:00", "17:00")),
            List.of(equipment("E1", SpecialityIntermediate.BLOOD, List.of("Hémogramme"), 10)));

    LabOutputIntermediate out = planner.planifyLab(in);

    assertEquals(30, out.getSchedule().get(0).getDuration());
  }

  @Test
  void efficiency_should_impact_next_sample_start() {
    // T1 efficacité 1.2 : S1 finit à 09:38 + 10min nettoyage → S2 peut commencer à 09:48
    LabInputIntermediate in =
        input(
            List.of(
                sample("S1", Priority.URGENT, "Hémogramme", 45, "09:00"),
                sample("S2", Priority.URGENT, "Hémogramme", 30, "09:00")),
            List.of(technician("T1", List.of(SpecialityIntermediate.BLOOD), 1.2, "08:00", "17:00")),
            List.of(equipment("E1", SpecialityIntermediate.BLOOD, List.of("Hémogramme"), 10)));

    LabOutputIntermediate out = planner.planifyLab(in);

    assertEquals(LocalTime.parse("09:48"), out.getSchedule().get(1).getStartTime());
  }
}
