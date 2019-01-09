package pl.north93.northplatform.antycheat.analysis.reaction;

/**
 * Reprezentuje poziom ostrzeżenia w monitorze.
 * Każdy {@link ITriggerLevel} posiada warunek podany podczas rejestrowania.
 */
public interface ITriggerLevel
{
    void registerListener(ITriggerListener listener);
}
