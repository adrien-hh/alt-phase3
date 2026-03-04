package org.alt.bo.input.intermediate;

import lombok.*;
import org.alt.bo.input.simple.Sample;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class SampleIntermediate extends Sample {
    private String analysisType;
    private PatientInfo patientInfo;
}
