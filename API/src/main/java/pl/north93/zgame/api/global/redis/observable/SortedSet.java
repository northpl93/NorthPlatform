package pl.north93.zgame.api.global.redis.observable;

import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

public interface SortedSet<K>
{
    void add(K key, double rank);

    void remove(K key);

    double get(K key);

    long getRank(K key); // ordered from low to high

    long getRevRank(K key); // ordered from high to low

    long count(double from, double to);

    Set<String> getRange(long from, long to); // ordered from low to high

    Set<String> getRevRange(long from, long to); // ordered from high to low

    Set<Pair<String, Long>> getRangeWithScores(long from, long to); // ordered from low to high

    Set<Pair<String, Long>> getRevRangeWithScores(long from, long to); // ordered from high to low

    void clear();
}
