package org.alt.bo.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;
import org.alt.bo.input.intermediate.EquipmentIntermediate;
import org.alt.bo.input.intermediate.SampleIntermediate;
import org.alt.bo.input.intermediate.TechnicianIntermediate;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class LabInputIntermediate {
    private List<SampleIntermediate> samples = new ArrayList<>();
    private List<TechnicianIntermediate> technicians = new ArrayList<>();
    @JsonAlias("equipment")
    private List<EquipmentIntermediate> equipments = new ArrayList<>();
}
