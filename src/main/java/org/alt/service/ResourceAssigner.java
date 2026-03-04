package org.alt.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.alt.bo.common.Assignment;
import org.alt.bo.input.simple.Equipment;
import org.alt.bo.input.simple.Sample;
import org.alt.bo.input.simple.Technician;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Getter
@Setter
public class ResourceAssigner {

    private final CompatibilityChecker compatibilityChecker;

    public ResourceAssigner() {
        this.compatibilityChecker = new CompatibilityChecker();
    }

    public Assignment findBestAssignment(
            Sample sample,
            List<Technician> technicians,
            List<Equipment> equipments,
            Map<String, LocalTime> technicianFreeAt,
            Map<String, LocalTime> equipmentFreeAt
    ) {
        Assignment best = null;

        for (Technician technician : technicians) {
            if (!compatibilityChecker.isTechCompatible(technician, sample)) continue;

            for (Equipment equipment : equipments) {
                if (!compatibilityChecker.isEquipCompatible(equipment, sample)) continue;

                LocalTime start = maxStartTime(
                        sample.getArrivalTime(),
                        technician.getStartTime(),
                        technicianFreeAt.get(technician.getId()),
                        equipmentFreeAt.get(equipment.getId())
                );

                if (best == null || start.isBefore(best.start())) {
                    best = new Assignment(technician, equipment, start);
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
}
