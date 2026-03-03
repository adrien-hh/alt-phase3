package org.alt.bo.input;

import lombok.*;

import java.time.LocalTime;

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
    private int analysisTime;   // duration in minutes
    private LocalTime arrivalTime;
    private String patientId;
}