package pl.north93.zgame.api.global.utils;

import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class MapCollector
{
    /**
     * Kolektor który zamiania strumień EntrySet na HashMapę.
     *
     * @param <K> typ klucza
     * @param <U> typ wartości
     * @return nowa mapa.
     */
    public static <K, U> Collector<Map.Entry<K, U>, ?, Map<K, U>> toMap()
    {
        return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);
    }
}
