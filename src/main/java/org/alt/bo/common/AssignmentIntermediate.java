package org.alt.bo.common;

import java.time.LocalTime;
import org.alt.bo.input.intermediate.EquipmentIntermediate;
import org.alt.bo.input.intermediate.TechnicianIntermediate;

public record AssignmentIntermediate(
    TechnicianIntermediate technician, EquipmentIntermediate equipment, LocalTime start) {}
