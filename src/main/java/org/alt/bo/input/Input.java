package org.alt.bo.input;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Input {
    private List<Sample> samples = new ArrayList<>();
    private List<Technician> technicians = new ArrayList<>();
    private List<Equipment> equipments = new ArrayList<>();
}
