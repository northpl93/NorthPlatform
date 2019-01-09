package pl.north93.northplatform.antycheat.cheat.fight;

import pl.north93.northplatform.antycheat.analysis.Violation;

public enum FightViolation implements Violation
{
    /**
     * Wszystkie naruszenia związane z częstotliwością bicia.
     */
    HIT_RATIO,
    /**
     * Wszystkie naruszenia związane z obranym celem ataku.
     */
    HIT_TARGET
}
