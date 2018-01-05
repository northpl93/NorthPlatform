package pl.north93.zgame.antycheat.cheat.movement;

import pl.north93.zgame.antycheat.analysis.Violation;

public enum MovementViolation implements Violation
{
    /**
     * Manipulacja polem onGround w pakietach ruchu.
     * Moze oznaczac fly (latanie na ziemi) lub nofall (onGround=true w powietrzu).
     */
    ON_GROUND_INCONSISTENCY
}
