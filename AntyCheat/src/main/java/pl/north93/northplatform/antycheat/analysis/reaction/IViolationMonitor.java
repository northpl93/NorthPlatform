package pl.north93.northplatform.antycheat.analysis.reaction;

import pl.north93.northplatform.antycheat.analysis.Violation;

/**
 * Reprezentuje zarejestrowany w systemie monitor, w którym mogą zostać dodane
 * poziomy na których zostaną wywołane listenery po spełnieniu warunku.
 */
public interface IViolationMonitor
{
    /**
     * @return Naruszenie które monitoruje ten monitor.
     */
    Violation getViolation();

    /**
     * @return Ilość ostatnich ticków brana pod uwagę podczas analizy.
     */
    int getTicks();

    /**
     * Tworzy nowy poziom z podanym warunkiem.
     * Do poziomu można dodać dowolną ilość warunków.
     *
     * @param condition Warunek wywołania danego poziomu.
     * @return Obiekt reprezentujący zarejestrowany poziom do którego można dodać listenery.
     */
    ITriggerLevel addLevel(TriggerCondition condition);

    default ITriggerLevel addLevel(final TriggerCondition condition, final ITriggerListener listener)
    {
        final ITriggerLevel level = this.addLevel(condition);
        level.registerListener(listener);
        return level;
    }
}
