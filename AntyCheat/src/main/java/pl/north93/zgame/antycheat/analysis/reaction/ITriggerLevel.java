package pl.north93.zgame.antycheat.analysis.reaction;

/**
 * Reprezentuje poziom ostrzeżenia w monitorze.
 * Każdy {@link ITriggerLevel} posiada warunek podany podczas rejestrowania.
 */
public interface ITriggerLevel
{
    void registerListener(ITriggerListener listener);
}
