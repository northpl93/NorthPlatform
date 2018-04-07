package pl.north93.zgame.api.global.redis.messaging.impl;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageFormat;
import org.msgpack.core.MessageUnpacker;

import pl.north93.zgame.api.global.redis.messaging.Template;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.api.global.redis.messaging.impl.element.ITemplateElement;

class TemplateImpl<T> implements Template<T>
{
    private final InstanceCreator<T>     instanceCreator;
    private final List<ITemplateElement> structure;

    public TemplateImpl(final Class<T> templateClass, final List<ITemplateElement> structure)
    {
        this.instanceCreator = setUpCreator(templateClass);
        this.structure = structure;
    }

    private static <T> InstanceCreator<T> setUpCreator(final Class<T> templateClass)
    {
        try
        {
            templateClass.getConstructor(); // probujemy uzyskac konstruktor bez argumentów
            return new MethodHandleConstructorCreator<>(templateClass);
        }
        catch (final Exception e)
        {
            return new UnsafeCreator<>(templateClass);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void serializeObject(final TemplateManager templateManager, final MessageBufferPacker packer, final T object) throws Exception
    {
        for (final ITemplateElement templateElement : this.structure)
        {
            //API.debug("TemplateImpl :: TemplateElement :: " + templateElement);
            final Object value = templateElement.get(object);
            if (value != null)
            {
                templateElement.getTemplate().serializeObject(templateManager, packer, value);
            }
            else
            {
                packer.packNil();
            }
        }
    }

    @Override
    public T deserializeObject(final TemplateManager templateManager, final MessageUnpacker unpacker) throws Exception
    {
        try
        {
            final T instance = this.instanceCreator.newInstance();
            for (final ITemplateElement templateElement : this.structure)
            {
                if (unpacker.getNextFormat() == MessageFormat.NIL)
                {
                    unpacker.skipValue();
                    continue; // next value is nil so we not try to deserialize it
                }
                templateElement.set(instance, templateElement.getTemplate().deserializeObject(templateManager, unpacker));
            }

            return instance;
        }
        catch (final Throwable e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("instanceCreator", this.instanceCreator).append("structure", this.structure).toString();
    }
}
