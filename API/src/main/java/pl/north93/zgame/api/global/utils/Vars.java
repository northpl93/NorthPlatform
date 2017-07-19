package pl.north93.zgame.api.global.utils;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

public final class Vars <T>
{
    private static final Vars<?> EMPTY = new Vars<Object>(ImmutableMap.of());
    
    private final Map<String, T> vars;
    
    private Vars(Map<String, T> vars)
    {
        this.vars = vars;
    }
    
    public T getValue(String key)
    {
        return vars.get(key);
    }
    
    public boolean hasKey(String key)
    {
        return getValue(key) != null;
    }
    
    public int size()
    {
        return vars.size();
    }
    
    public Map<String, T> asMap()
    {
        return new HashMap<>(vars);
    }
    
    public Vars<T> and(String key, T value)
    {
        Preconditions.checkArgument(key != null, "key cannot be null");
        Preconditions.checkArgument(value != null, "value cannot be null");
        
        Map<String, T> newMap = new HashMap<>(vars.size());
        newMap.put(key, value);
        return new Vars<T>(newMap);
    }
    
    public Vars<T> and(Vars<T> vars)
    {
        Preconditions.checkArgument(vars != null);
        
        Map<String, T> newMap = new HashMap<>(vars.size());
        newMap.putAll(vars.vars);
        return new Vars<T>(newMap);
    }
    
    public Vars<T> withoutKey(String key)
    {
        Preconditions.checkArgument(key != null, "key cannot be null");
        
        Map<String, T> newMap = new HashMap<>(vars.size());
        newMap.remove(key);
        return new Vars<T>(newMap);
    }
    
    public Vars<T> withoutValue(T value)
    {
        Preconditions.checkArgument(value != null, "value cannot be null");
        
        Map<String, T> newMap = new HashMap<>(vars.size());
        newMap.values().removeIf(v -> v.equals(value));
        return new Vars<T>(newMap);
    }
    
    public static <T> Vars<T> of(String key, T value)
    {
        Preconditions.checkArgument(key != null, "key cannot be null");
        Preconditions.checkArgument(value != null, "value cannot be null");
        
        return new Vars<T>(ImmutableMap.of(key, value));
    }
    
    @SuppressWarnings("unchecked")
    public static <T> Vars<T> empty()
    {
        return (Vars<T>) EMPTY;
    }
}
