package pl.north93.zgame.api.global.redis.messaging;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public interface TemplateManager
{
    void registerTemplate(Class<?> clazz, Template template);

    <T> Template<T> getTemplate(Class<T> clazz); // bez generic type

    <T> Template<?> getTemplate(Class<T> clazz, ParameterizedType genericType); // z generic type np. Lista czy coś

    <T> Template<?> getTemplate(Class<T> clazz, Class<?> genericType); // z generic type np. Lista czy coś

    byte[] serialize(Object object);

    byte[] serialize(Class<?> clazz, Object object);

    <T> T deserialize(Class<T> clazz, byte[] bytes);

    <T> byte[] serializeList(Class<T> listOf, List<T> list);

    <T> List<T> deserializeList(Class<T> listOf, byte[] bytes);
}
