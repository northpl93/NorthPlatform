package pl.arieals.api.minigame.shared.api.statistics;

import org.bson.Document;

/**
 * Reprezentuje jednostke przechowywanych statystyk.
 */
public interface IStatisticUnit<T>
{
    T getValue();

    void setValue(T newValue);

    void toDocument(Document document);
}
