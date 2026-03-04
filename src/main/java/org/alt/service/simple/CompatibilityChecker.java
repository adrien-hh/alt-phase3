package org.alt.service.simple;

import org.alt.bo.input.simple.Equipment;
import org.alt.bo.input.simple.Sample;
import org.alt.bo.input.simple.Technician;

public class CompatibilityChecker {

  public boolean isEquipCompatible(Equipment equipment, Sample sample) {
    return equipment.isAvailable() && equipment.getType() == sample.getType();
  }

  public boolean isTechCompatible(Technician technician, Sample sample) {
    return technician.getSpeciality().supports(sample.getType());
  }
}
