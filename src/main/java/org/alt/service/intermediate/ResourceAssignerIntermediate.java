package org.alt.service.intermediate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.alt.bo.common.AssignmentIntermediate;
import org.alt.bo.input.intermediate.EquipmentIntermediate;
import org.alt.bo.input.intermediate.SampleIntermediate;
import org.alt.bo.input.intermediate.TechnicianIntermediate;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

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
            Map<String, LocalTime> equipmentFreeAt
    ) {
        AssignmentIntermediate best = null;

        for (TechnicianIntermediate t : technicians) {
            if (!compatibilityChecker.isTechCompatible(t, sample)) continue;

            for (EquipmentIntermediate e : equipments) {
                if (!compatibilityChecker.isEquipCompatible(e, sample)) continue;

                LocalTime start = maxStartTime(
                        sample.getArrivalTime(),
                        t.getStartTime(),
                        technicianFreeAt.get(t.getId()),
                        equipmentFreeAt.get(e.getId())
                );

                if (best == null || start.isBefore(best.start())) {
                    best = new AssignmentIntermediate(t, e, start);
                }
            }
        }
        return best;
    }

    private LocalTime maxStartTime(LocalTime... times) {
        LocalTime max = times[0];
        for (int i = 1; i < times.length; i++) {
            if (times[i].isAfter(max)) max = times[i];
        }
        return max;
    }

    public void initMapping(List<EquipmentIntermediate> equipments) {
        compatibilityChecker.buildMapping(equipments);
    }

    public record Assignment(TechnicianIntermediate technician, EquipmentIntermediate equipment, LocalTime start) {}
}
