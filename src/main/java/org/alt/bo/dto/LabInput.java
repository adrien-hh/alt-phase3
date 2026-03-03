package org.alt.bo.dto;

import lombok.*;
import org.alt.bo.input.Equipment;
import org.alt.bo.input.Sample;
import org.alt.bo.input.Technician;

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
