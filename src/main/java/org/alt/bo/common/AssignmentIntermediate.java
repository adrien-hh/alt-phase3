package org.alt.bo.common;

import org.alt.bo.input.intermediate.EquipmentIntermediate;
import org.alt.bo.input.intermediate.TechnicianIntermediate;

import java.time.LocalTime;

public record AssignmentIntermediate(TechnicianIntermediate technician, EquipmentIntermediate equipment,
                                     LocalTime start) {
}