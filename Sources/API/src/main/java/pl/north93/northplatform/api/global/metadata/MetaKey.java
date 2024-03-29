package pl.north93.northplatform.api.global.metadata;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import pl.north93.serializer.platform.annotations.NorthCustomTemplate;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NorthCustomTemplate(MetaKeyTemplate.class)
public final class MetaKey
{
    private static final Map<String, MetaKey> KEY_CACHE = new HashMap<>();

    public static MetaKey get(final String keyName)
    {
        if (StringUtils.isEmpty(keyName))
        {
            throw new IllegalArgumentException("keyName can't be null");
        }

        final String normalizedKey = keyName.toLowerCase(Locale.ROOT); // klucz przyjmie wielkosc znaków jak przy pierwszym wywolaniu
        return KEY_CACHE.computeIfAbsent(normalizedKey, k -> new MetaKey(keyName));
    }

    // Single class field //
    private final String key;
}
