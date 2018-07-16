package pl.north93.zgame.api.global.utils.lang;

import java.util.Collections;
import java.util.Comparator;
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
    
    public static <T extends Comparable<? super T>> void insertSorted(List<T> list, T element)
    {
        int index = Collections.binarySearch(list, element);
        list.add(index < 0 ? -(index + 1) : index + 1, element);
    }
    
    public static <T> void insertSorted(List<T> list, T element, Comparator<? super T> comparator)
    {
        int index = Collections.binarySearch(list, element, comparator);
        list.add(index < 0 ? -(index + 1) : index + 1, element);
    }
}
