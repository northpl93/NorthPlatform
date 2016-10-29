package pl.north93.zgame.api.global.redis.messaging;

import java.lang.reflect.ParameterizedType;

public interface TemplateGeneric<T> extends Template<T>, Cloneable
{
    Template<T> setGenericType(ParameterizedType type);

    Template<T> setGenericType(Class<?>... genericTypes);
}
