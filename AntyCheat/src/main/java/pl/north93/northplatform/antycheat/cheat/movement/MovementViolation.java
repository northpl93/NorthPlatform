package pl.north93.northplatform.antycheat.cheat.movement;

import pl.north93.northplatform.antycheat.analysis.Violation;

public enum MovementViolation implements Violation
{
    /**
     * Manipulacja polem onGround w pakietach ruchu.
     * Moze oznaczac fly (latanie na ziemi), nofall (onGround=true w powietrzu) lub spider (chodzenie po scianach).
     */
    ON_GROUND_INCONSISTENCY,
    /**
     * Manipulacja wysokością skoku, podnoszenie się podczas opadania.
     */
    SURVIVAL_FLY
}
