package pl.north93.zgame.api.global.utils.lang;

import java.util.List;
import java.util.Optional;

public final class ListUtils
{
    private ListUtils()
    {
    }
    
    public static <T> T getOrNull(List<? extends T> list, int index)
    {
        if ( list.size() > index )
        {
            return list.get(index);
        }
        
        return null;
    }
    
    public static <T> Optional<T> getIfExists(List<? extends T> list, int index)
    {
        return Optional.ofNullable(getOrNull(list, index));
    }
}
