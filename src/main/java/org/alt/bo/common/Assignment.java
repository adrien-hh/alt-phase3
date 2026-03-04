package org.alt.bo.common;

import java.time.LocalTime;
import org.alt.bo.input.simple.Equipment;
import org.alt.bo.input.simple.Technician;

public record Assignment(Technician technician, Equipment equipment, LocalTime start) {}
