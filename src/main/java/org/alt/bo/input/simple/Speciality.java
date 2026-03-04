package org.alt.bo.input.simple;

import org.alt.bo.input.Type;

public enum Speciality {
  BLOOD,
  URINE,
  TISSUE,
  GENERAL;

  public boolean supports(Type type) {
    if (this == GENERAL) {
      return true;
    }
    return this.name().equals(type.name());
  }
}
