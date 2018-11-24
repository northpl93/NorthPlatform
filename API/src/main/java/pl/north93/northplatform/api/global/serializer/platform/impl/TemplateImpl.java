package pl.north93.northplatform.api.global.serializer.platform.impl;

import java.util.List;

import lombok.ToString;
import pl.north93.northplatform.api.global.serializer.platform.context.DeserializationContext;
import pl.north93.northplatform.api.global.serializer.platform.context.SerializationContext;
import pl.north93.northplatform.api.global.serializer.platform.template.Template;
import pl.north93.northplatform.api.global.serializer.platform.FieldInfo;
import pl.north93.northplatform.api.global.serializer.platform.InstanceCreator;
import pl.north93.northplatform.api.global.serializer.platform.template.ITemplateElement;

@ToString
@SuppressWarnings("unchecked")
/*default*/ class TemplateImpl<T> implements Template<T, SerializationContext, DeserializationContext>
{
    private final InstanceCreator<T>     instanceCreator;
    private final List<ITemplateElement> structure;

    public TemplateImpl(final InstanceCreator<T> instanceCreator, final List<ITemplateElement> structure)
    {
        this.instanceCreator = instanceCreator;
        this.structure = structure;
    }

    @Override
    public void serialise(final SerializationContext context, final FieldInfo field, final T object) throws Exception
    {
        context.enterObject(field);
        for (final ITemplateElement templateElement : this.structure)
        {
            //API.debug("TemplateImpl :: TemplateElement :: " + templateElement);
            final Object value = templateElement.get(object);
            if (value != null)
            {
                final Template template = templateElement.getTemplate();
                template.serialise(context, templateElement.getFieldInfo(), value);
            }
            else
            {
                context.writeNull(templateElement.getFieldInfo());
            }
        }
        context.exitObject(field);
    }

    @Override
    public T deserialize(final DeserializationContext context, final FieldInfo field) throws Exception
    {
        context.enterObject(field);
        try
        {
            final T instance = this.instanceCreator.newInstance();
            for (final ITemplateElement templateElement : this.structure)
            {
                if (context.trySkipNull(templateElement.getFieldInfo()))
                {
                    continue; // next value is null so we not try to deserialize it
                }

                final Template template = templateElement.getTemplate();
                templateElement.set(instance, template.deserialize(context, templateElement.getFieldInfo()));
            }

            return instance;
        }
        catch (final Exception e)
        {
            throw new RuntimeException("Exception thrown while processing template " + this, e);
        }
        finally
        {
            context.exitObject(field);
        }
    }
}
