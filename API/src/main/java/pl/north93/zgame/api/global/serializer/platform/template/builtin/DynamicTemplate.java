package pl.north93.zgame.api.global.serializer.platform.template.builtin;

import java.lang.reflect.Type;

import pl.north93.zgame.api.global.serializer.platform.FieldInfo;
import pl.north93.zgame.api.global.serializer.platform.context.DeserializationContext;
import pl.north93.zgame.api.global.serializer.platform.context.SerializationContext;
import pl.north93.zgame.api.global.serializer.platform.template.Template;
import pl.north93.zgame.api.global.serializer.platform.template.TemplateEngine;
import pl.north93.zgame.api.global.serializer.platform.template.TemplateFilter;

public class DynamicTemplate implements Template<Object, SerializationContext, DeserializationContext>
{
    public static final class DynamicTemplateFilter implements TemplateFilter
    {
        @Override
        public int getPriority()
        {
            return 10;
        }

        @Override
        public boolean isApplicableTo(final TemplateEngine templateEngine, final Type type)
        {
            return templateEngine.isNeedsDynamicResolution(type);
        }
    }

    @Override
    public void serialise(final SerializationContext context, final FieldInfo field, final Object object) throws Exception
    {
        context.writeDynamicTypedField(field, object);
    }

    @Override
    public Object deserialize(final DeserializationContext context, final FieldInfo field) throws Exception
    {
        return context.readDynamicTypedField(field);
    }
}
