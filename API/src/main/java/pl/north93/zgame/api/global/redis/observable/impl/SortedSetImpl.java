package pl.north93.zgame.api.global.redis.observable.impl;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javafx.util.Pair;
import pl.north93.zgame.api.global.redis.observable.SortedSet;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

class SortedSetImpl<K> implements SortedSet<K>
{
    private final ObservationManagerImpl observationManager;
    private final String                 prefix;

    public SortedSetImpl(final ObservationManagerImpl observationManager, final String prefix)
    {
        this.observationManager = observationManager;
        this.prefix = prefix;
    }

    @Override
    public void add(final K key, final double rank)
    {
        try (final Jedis jedis = this.observationManager.getJedis().getResource())
        {
            jedis.zadd(this.prefix, rank, key.toString());
        }
    }

    @Override
    public void remove(final K key)
    {
        try (final Jedis jedis = this.observationManager.getJedis().getResource())
        {
            jedis.zrem(this.prefix, key.toString());
        }
    }

    @Override
    public double get(final K key)
    {
        try (final Jedis jedis = this.observationManager.getJedis().getResource())
        {
            return jedis.zscore(this.prefix, key.toString());
        }
    }

    @Override
    public long getRank(final K key)
    {
        try (final Jedis jedis = this.observationManager.getJedis().getResource())
        {
            final Long zrank = jedis.zrank(this.prefix, key.toString());
            if (zrank == null)
            {
                return 0;
            }
            return zrank;
        }
    }

    @Override
    public long getRevRank(final K key)
    {
        try (final Jedis jedis = this.observationManager.getJedis().getResource())
        {
            final Long zrevrank = jedis.zrevrank(this.prefix, key.toString());
            if (zrevrank == null)
            {
                return 0;
            }
            return zrevrank;
        }
    }

    @Override
    public long count(final double from, final double to)
    {
        try (final Jedis jedis = this.observationManager.getJedis().getResource())
        {
            return jedis.zcount(this.prefix, from, to);
        }
    }

    @Override
    public Set<String> getRange(final long from, final long to)
    {
        try (final Jedis jedis = this.observationManager.getJedis().getResource())
        {
            return jedis.zrange(this.prefix, from, to);
        }
    }

    @Override
    public Set<String> getRevRange(final long from, final long to)
    {
        try (final Jedis jedis = this.observationManager.getJedis().getResource())
        {
            return jedis.zrevrange(this.prefix, from, to);
        }
    }

    @Override
    public Set<Pair<String, Long>> getRangeWithScores(final long from, final long to)
    {
        try (final Jedis jedis = this.observationManager.getJedis().getResource())
        {
            return jedis.zrangeWithScores(this.prefix, from, to).stream().map(this::tupleToPair).collect(Collectors.toSet());
        }
    }

    @Override
    public Set<Pair<String, Long>> getRevRangeWithScores(final long from, final long to)
    {
        try (final Jedis jedis = this.observationManager.getJedis().getResource())
        {
            return jedis.zrevrangeWithScores(this.prefix, from, to).stream().map(this::tupleToPair).collect(Collectors.toSet());
        }
    }

    private Pair<String, Long> tupleToPair(final Tuple tuple) // used by *WithScore
    {
        return new Pair<>(tuple.getElement(), (long) tuple.getScore());
    }

    @Override
    public void clear()
    {
        try (final Jedis jedis = this.observationManager.getJedis().getResource())
        {
            jedis.del(this.prefix);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("prefix", this.prefix).toString();
    }
}
