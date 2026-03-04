package org.alt.bo.input.simple;

import lombok.*;
import org.alt.bo.input.Type;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
public class Equipment {
    private String id;
    private String name;
    private Type type;
    private boolean available = true;
}
