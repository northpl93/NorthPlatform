package pl.north93.zgame.api.global.utils;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class Vars <T>
{
    private static final Vars<?> EMPTY = new Vars<>(ImmutableMap.of());
    
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
        return vars.containsKey(key);
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
        
        Map<String, T> newMap = new HashMap<>(this.vars);
        newMap.put(key, value);
        return new Vars<>(newMap);
    }
    
    public Vars<T> and(Vars<T> vars)
    {
        Preconditions.checkArgument(vars != null);
        
        Map<String, T> newMap = new HashMap<>(this.vars);
        newMap.putAll(vars.vars);
        return new Vars<>(newMap);
    }
    
    public Vars<T> withoutKey(String key)
    {
        Preconditions.checkArgument(key != null, "key cannot be null");
        
        Map<String, T> newMap = new HashMap<>(this.vars);
        newMap.remove(key);
        return new Vars<>(newMap);
    }
    
    public Vars<T> withoutValue(T value)
    {
        Preconditions.checkArgument(value != null, "value cannot be null");
        
        Map<String, T> newMap = new HashMap<>(this.vars);
        newMap.values().removeIf(v -> v.equals(value));
        return new Vars<>(newMap);
    }
    
    public static <T> Vars<T> of(String key, T value)
    {
        Preconditions.checkArgument(key != null, "key cannot be null");
        Preconditions.checkArgument(value != null, "value cannot be null");
        
        return new Vars<>(ImmutableMap.of(key, value));
    }
    
    @SuppressWarnings("unchecked")
    public static <T> Vars<T> empty()
    {
        return (Vars<T>) EMPTY;
    }

    public static <T> Builder<T> builder()
    {
        return new Builder<>();
    }

    public static final class Builder<T>
    {
        private final Map<String, T> vars = new HashMap<>();

        public Builder<T> and(final String key, final T value)
        {
            this.vars.put(key, value);
            return this;
        }

        public Builder<T> and(final Vars<T> vars)
        {
            this.vars.putAll(vars.vars);
            return this;
        }

        public Vars<T> build()
        {
            return new Vars<>(this.vars);
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("vars", this.vars).toString();
        }
    }
}
