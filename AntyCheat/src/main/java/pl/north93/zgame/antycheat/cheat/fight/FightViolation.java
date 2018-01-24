package pl.north93.zgame.antycheat.cheat.fight;

import pl.north93.zgame.antycheat.analysis.Violation;

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
