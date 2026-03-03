package org.alt.bo.input;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
public class Sample {
    private String id;
    private Type type;
    private Priority priority;
    private Integer analysisTime;   // duration in minutes
    private String arrivalTime;
    private String patientId;
}