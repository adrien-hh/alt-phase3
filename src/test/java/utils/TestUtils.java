package utils;

import java.time.LocalTime;
import java.util.List;
import org.alt.bo.dto.LabInputIntermediate;
import org.alt.bo.input.Priority;
import org.alt.bo.input.intermediate.EquipmentIntermediate;
import org.alt.bo.input.intermediate.SampleIntermediate;
import org.alt.bo.input.intermediate.SpecialityIntermediate;
import org.alt.bo.input.intermediate.TechnicianIntermediate;

public class TestUtils {

  public static SampleIntermediate sample(
      String id, Priority priority, String analysisType, int analysisTime, String arrivalTime) {
    SampleIntermediate s = new SampleIntermediate();
    s.setId(id);
    s.setPriority(priority);
    s.setAnalysisType(analysisType);
    s.setAnalysisTime(analysisTime);
    s.setArrivalTime(LocalTime.parse(arrivalTime));
    return s;
  }

  public static TechnicianIntermediate technician(
      String id,
      List<SpecialityIntermediate> specialities,
      double efficiency,
      String start,
      String end) {
    TechnicianIntermediate t = new TechnicianIntermediate();
    t.setId(id);
    t.setSpecialities(specialities);
    t.setEfficiency(efficiency);
    t.setStartTime(LocalTime.parse(start));
    t.setEndTime(LocalTime.parse(end));
    return t;
  }

  public static EquipmentIntermediate equipment(
      String id,
      SpecialityIntermediate speciality,
      List<String> compatibleTypes,
      int cleaningTime) {
    EquipmentIntermediate e = new EquipmentIntermediate();
    e.setId(id);
    e.setSpecialityIntermediate(speciality);
    e.setCompatibleTypes(compatibleTypes);
    e.setCleaningTime(cleaningTime);
    e.setAvailable(true);
    return e;
  }

  public static LabInputIntermediate input(
      List<SampleIntermediate> samples,
      List<TechnicianIntermediate> technicians,
      List<EquipmentIntermediate> equipments) {
    LabInputIntermediate input = new LabInputIntermediate();
    input.setSamples(samples);
    input.setTechnicians(technicians);
    input.setEquipments(equipments);
    return input;
  }
}
