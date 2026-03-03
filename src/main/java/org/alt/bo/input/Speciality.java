package org.alt.bo.input;

public enum Speciality {
    BLOOD,
    URINE,
    TISSUE,
    GENERAL;

    public boolean supports(Type type) {
        if (this == GENERAL) return true;
        return this.name().equals(type.name());
    }
}
