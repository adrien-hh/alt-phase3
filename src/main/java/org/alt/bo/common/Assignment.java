package org.alt.bo.common;

import org.alt.bo.input.simple.Equipment;
import org.alt.bo.input.simple.Technician;

import java.time.LocalTime;

public record Assignment(Technician technician, Equipment equipment, LocalTime start) {
}
