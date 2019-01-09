package pl.north93.northplatform.api.global.utils.lang;

import javax.annotation.Nullable;

import java.util.Collection;
import java.util.function.Function;

public final class CollectionUtils
{
    private CollectionUtils()
    {
    }

    public static @Nullable <T> T findInCollection(final Collection<T> collection, final Function<T, String> mapper, final String search)
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
