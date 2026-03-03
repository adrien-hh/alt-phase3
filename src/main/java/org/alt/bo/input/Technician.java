package org.alt.bo.input;

import lombok.*;

import java.time.LocalTime;

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
    private LocalTime startTime;
    private LocalTime endTime;
}
