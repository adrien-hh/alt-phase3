package org.alt.bo.input;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
public class Equipment {
    private String id;
    private String name;
    private Type type;
    private boolean available;
}
