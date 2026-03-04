package org.alt.service.intermediate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.alt.bo.input.intermediate.EquipmentIntermediate;
import org.alt.bo.input.intermediate.SampleIntermediate;
import org.alt.bo.input.intermediate.SpecialityIntermediate;
import org.alt.bo.input.intermediate.TechnicianIntermediate;

@Getter
@Setter
public class CompatibilityCheckerIntermediate {

  private Map<SpecialityIntermediate, List<String>> mapping;

  public void buildMapping(List<EquipmentIntermediate> equipments) {
    this.mapping =
        equipments.stream()
            .filter(equipment -> equipment.getSpecialityIntermediate() != null)
            .filter(
                equipment ->
                    equipment.getCompatibleTypes() != null
                        && !equipment.getCompatibleTypes().isEmpty())
            .collect(
                Collectors.toMap(
                    EquipmentIntermediate::getSpecialityIntermediate,
                    EquipmentIntermediate::getCompatibleTypes));
  }

  public boolean isTechCompatible(TechnicianIntermediate technician, SampleIntermediate sample) {
    return technician.getSpecialities().stream()
        .anyMatch(
            speciality ->
                mapping.getOrDefault(speciality, List.of()).contains(sample.getAnalysisType()));
  }

  public boolean isEquipCompatible(EquipmentIntermediate equipment, SampleIntermediate sample) {
    return mapping
        .getOrDefault(equipment.getSpecialityIntermediate(), List.of())
        .contains(sample.getAnalysisType());
  }
}
