package org.alt.bo.input;

/**
 * Represents the priority status of a sample
 * Golden rule : STAT > URGENT > ROUTINE
 *
 * Si nouveau STAT arrive :
 * 1. Vérifier si analyse interruptible en cours
 * 2. Réquisitionner technicien/équipement approprié
 * 3. Reprogrammer analyse interrompue
 * 4. Démarrer STAT immédiatement
 */
public enum Priority {
    STAT,
    URGENT,
    ROUTINE
}
