package pl.north93.northplatform.api.global.utils.lang;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ListUtils
{
    public <T> T getOrNull(final List<? extends T> list, final int index)
    {
        if ( list.size() > index )
        {
            return list.get(index);
        }
        
        return null;
    }
    
    public <T> Optional<T> getIfExists(final List<? extends T> list, final int index)
    {
        return Optional.ofNullable(getOrNull(list, index));
    }
    
    public <T extends Comparable<? super T>> void insertSorted(final List<T> list, final T element)
    {
        final int index = Collections.binarySearch(list, element);
        list.add(index < 0 ? -(index + 1) : index + 1, element);
    }
    
    public <T> void insertSorted(final List<T> list, final T element, final Comparator<? super T> comparator)
    {
        final int index = Collections.binarySearch(list, element, comparator);
        list.add(index < 0 ? -(index + 1) : index + 1, element);
    }
}
