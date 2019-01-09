package pl.north93.northplatform.api.global.redis.observable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public final class ObjectKey
{
    private final String key;

    public ObjectKey(final String prefix, final ObjectKey key)
    {
        this(prefix + key.getKey());
    }
}
