package org.alt.bo.dto;

import lombok.*;
import org.alt.bo.input.simple.Equipment;
import org.alt.bo.input.simple.Sample;
import org.alt.bo.input.simple.Technician;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class LabInput {
    private List<Sample> samples = new ArrayList<>();
    private List<Technician> technicians = new ArrayList<>();
    private List<Equipment> equipments = new ArrayList<>();
}
