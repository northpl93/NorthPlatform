package pl.north93.zgame.antycheat.analysis.reaction;

/**
 * Reprezentuje warunek wywołania danego {@link ITriggerLevel}.
 */
public interface TriggerCondition
{
    /**
     * Sprawdza czy warunek jest spełniony na podstawie kontekstu.
     *
     * @param context Kontekst do sprawdzenia.
     * @return True jeśli warunek jest spełniony. False w przeciwnym wypadku.
     */
    boolean isTriggered(TriggerCheckContext context);
}
