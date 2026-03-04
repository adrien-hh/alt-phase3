package org.alt.service.intermediate;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.alt.bo.common.AssignmentIntermediate;
import org.alt.bo.common.LunchState;
import org.alt.bo.input.Priority;
import org.alt.bo.input.intermediate.EquipmentIntermediate;
import org.alt.bo.input.intermediate.SampleIntermediate;
import org.alt.bo.input.intermediate.TechnicianIntermediate;

@RequiredArgsConstructor
@Getter
@Setter
public class ResourceAssignerIntermediate {

  private final CompatibilityCheckerIntermediate compatibilityChecker;

  public ResourceAssignerIntermediate() {
    this.compatibilityChecker = new CompatibilityCheckerIntermediate();
  }

  public AssignmentIntermediate findBestAssignment(
      SampleIntermediate sample,
      List<TechnicianIntermediate> technicians,
      List<EquipmentIntermediate> equipments,
      Map<String, LocalTime> technicianFreeAt,
      Map<String, LocalTime> equipmentFreeAt,
      Map<String, LunchState> lunchStates) {
    AssignmentIntermediate best = null;

    for (TechnicianIntermediate technician : technicians) {
      if (!compatibilityChecker.isTechCompatible(technician, sample)) {
        continue;
      }

      for (EquipmentIntermediate equipment : equipments) {
        if (!compatibilityChecker.isEquipCompatible(equipment, sample)) {
          continue;
        }

        LocalTime start =
            maxStartTime(
                sample.getArrivalTime(),
                technician.getStartTime(),
                technicianFreeAt.get(technician.getId()),
                equipmentFreeAt.get(equipment.getId()));

        start = adjustForLunch(start, lunchStates.get(technician.getId()));

        // Constraint : STAT analysis starts 30 minutes max after arrival
        if (sample.getPriority() == Priority.STAT) {
          LocalTime deadline = sample.getArrivalTime().plusMinutes(30);
          if (start.isAfter(deadline)) {
            continue;
          }
        }

        // Checking that analysis ends before technician's shift end
        long duration = Math.round(sample.getAnalysisTime() / technician.getEfficiency());
        LocalTime end = start.plusMinutes(duration);
        if (end.isAfter(technician.getEndTime())) {
          continue;
        }

        if (best == null || start.isBefore(best.start())) {
          best = new AssignmentIntermediate(technician, equipment, start);
        }
      }
    }
    return best;
  }

  private LocalTime adjustForLunch(LocalTime start, LunchState lunch) {
    if (!lunch.taken() && LunchState.isInLunchWindow(start)) {
      return LunchState.schedule(start).end();
    }
    if (lunch.taken() && lunch.isDuringLunch(start)) {
      return lunch.end();
    }
    return start;
  }

  private LocalTime maxStartTime(LocalTime... times) {
    LocalTime max = times[0];
    for (int i = 1; i < times.length; i++) {
      if (times[i].isAfter(max)) {
        max = times[i];
      }
    }
    return max;
  }

  public void initMapping(List<EquipmentIntermediate> equipments) {
    compatibilityChecker.buildMapping(equipments);
  }
}
