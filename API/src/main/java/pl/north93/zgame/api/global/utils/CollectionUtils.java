package pl.north93.zgame.api.global.utils;

import java.util.Collection;
import java.util.function.Function;

public final class CollectionUtils
{
    private CollectionUtils()
    {
    }

    public static <T> T findInCollection(final Collection<T> collection, final Function<T, String> mapper, final String search)
    {
        for (final T entry : collection)
        {
            if (search.equals(mapper.apply(entry)))
            {
                return entry;
            }
        }
        return null;
    }
}
