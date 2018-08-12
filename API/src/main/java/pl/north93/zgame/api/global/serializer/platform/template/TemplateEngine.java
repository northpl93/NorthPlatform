package pl.north93.zgame.api.global.serializer.platform.template;

import java.lang.reflect.Type;

import pl.north93.zgame.api.global.serializer.platform.context.DeserializationContext;
import pl.north93.zgame.api.global.serializer.platform.context.SerializationContext;

public interface TemplateEngine
{
    Class<?> findClass(String name);

    boolean isNeedsDynamicResolution(Type type);

    void register(TemplateFilter filter, Template<?, ?, ?> template);

    Template<Object, SerializationContext, DeserializationContext> getTemplate(Type type);
}
