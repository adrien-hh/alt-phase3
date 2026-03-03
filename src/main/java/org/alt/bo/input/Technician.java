package org.alt.bo.input;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
public class Technician {
    private String id;
    private String name;
    private Speciality speciality;
    private String startTime;
    private String endTime;
}
