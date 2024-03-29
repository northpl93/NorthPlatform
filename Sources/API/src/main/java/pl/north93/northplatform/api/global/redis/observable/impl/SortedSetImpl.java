package pl.north93.northplatform.api.global.redis.observable.impl;

import static pl.north93.northplatform.api.global.utils.lang.StringUtils.asString;
import static pl.north93.northplatform.api.global.utils.lang.StringUtils.toBytes;


import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.tuple.Pair;

import io.lettuce.core.Range;
import io.lettuce.core.ScoredValue;
import io.lettuce.core.api.sync.RedisCommands;
import pl.north93.northplatform.api.global.redis.observable.SortedSet;
import pl.north93.northplatform.api.global.utils.lang.StringUtils;

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
        final RedisCommands<String, byte[]> redis = this.observationManager.getRedis();
        redis.zadd(this.prefix, rank, toBytes(key.toString()));
    }

    @Override
    public void remove(final K key)
    {
        final RedisCommands<String, byte[]> redis = this.observationManager.getRedis();
        redis.zrem(this.prefix, toBytes(key.toString()));
    }

    @Override
    public double get(final K key)
    {
        final RedisCommands<String, byte[]> redis = this.observationManager.getRedis();
        return redis.zscore(this.prefix, toBytes(key.toString()));
    }

    @Override
    public long getRank(final K key)
    {
        final RedisCommands<String, byte[]> redis = this.observationManager.getRedis();
        final Long zrank = redis.zrank(this.prefix, toBytes(key.toString()));
        if (zrank == null)
        {
            return 0;
        }
        return zrank;
    }

    @Override
    public long getRevRank(final K key)
    {
        final RedisCommands<String, byte[]> redis = this.observationManager.getRedis();
        final Long zrevrank = redis.zrevrank(this.prefix, toBytes(key.toString()));
        return Objects.requireNonNullElse(zrevrank, 0L);
    }

    @Override
    public long count(final double from, final double to)
    {
        final RedisCommands<String, byte[]> redis = this.observationManager.getRedis();
        return redis.zcount(this.prefix, Range.create(from, to));
    }

    @Override
    public Set<String> getRange(final long from, final long to)
    {
        final RedisCommands<String, byte[]> redis = this.observationManager.getRedis();
        return redis.zrange(this.prefix, from, to).stream().map(StringUtils::asString).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getRevRange(final long from, final long to)
    {
        final RedisCommands<String, byte[]> redis = this.observationManager.getRedis();
        return redis.zrevrange(this.prefix, from, to).stream().map(StringUtils::asString).collect(Collectors.toSet());
    }

    @Override
    public Set<Pair<String, Long>> getRangeWithScores(final long from, final long to)
    {
        final RedisCommands<String, byte[]> redis = this.observationManager.getRedis();
        return redis.zrangeWithScores(this.prefix, from, to).stream().map(this::tupleToPair).collect(Collectors.toSet());
    }

    @Override
    public Set<Pair<String, Long>> getRevRangeWithScores(final long from, final long to)
    {
        final RedisCommands<String, byte[]> redis = this.observationManager.getRedis();
        return redis.zrevrangeWithScores(this.prefix, from, to).stream().map(this::tupleToPair).collect(Collectors.toSet());
    }

    private Pair<String, Long> tupleToPair(final ScoredValue<byte[]> value) // used by *WithScore
    {
        return Pair.of(asString(value.getValue()), (long) value.getScore());
    }

    @Override
    public void clear()
    {
        final RedisCommands<String, byte[]> redis = this.observationManager.getRedis();
        redis.del(this.prefix);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("prefix", this.prefix).toString();
    }
}
