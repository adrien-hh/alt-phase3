package org.alt.bo.input.simple;

import java.time.LocalTime;
import lombok.*;

@NoArgsConstructor
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
