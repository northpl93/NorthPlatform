package pl.north93.zgame.api.global.redis.messaging.impl;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageFormat;
import org.msgpack.core.MessageUnpacker;

import pl.north93.zgame.api.global.redis.messaging.Template;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.api.global.redis.messaging.impl.element.ITemplateElement;

public class TemplateImpl<T> implements Template<T>
{
    private static final Lookup          LOOKUP      = MethodHandles.lookup();
    private static final MethodType      VOID_TYPE   = MethodType.methodType(void.class);
    private static final MethodType      OBJECT_TYPE = MethodType.methodType(Object.class);
    private final MethodHandle           constructor;
    private final List<ITemplateElement> structure;

    public TemplateImpl(final Class<T> templateClass, final List<ITemplateElement> structure)
    {
        try
        {
            this.constructor = LOOKUP.findConstructor(templateClass, VOID_TYPE).asType(OBJECT_TYPE);
        }
        catch (final NoSuchMethodException | IllegalAccessException e)
        {
            throw new RuntimeException("Lookup failed. Can't find constructor.", e);
        }
        this.structure = structure;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void serializeObject(final TemplateManager templateManager, final MessageBufferPacker packer, final T object) throws Exception
    {
        for (final ITemplateElement templateElement : this.structure)
        {
            //API.debug("TemplateImpl :: TemplateElement :: " + templateElement);
            final Object value = templateElement.get(object);
            if (value == null)
            {
                if (! templateElement.isNullable())
                {
                    throw new NullPointerException("Field is not annotated by @MsgPackNullable");
                }
                packer.packNil();
            }
            else
            {
                templateElement.getTemplate().serializeObject(templateManager, packer, value);
            }
        }
    }

    @Override
    public T deserializeObject(final TemplateManager templateManager, final MessageUnpacker unpacker) throws Exception
    {
        try
        {
            @SuppressWarnings("unchecked")
            final T instance = (T) this.constructor.invokeExact();
            for (final ITemplateElement templateElement : this.structure)
            {
                if (templateElement.isNullable() && unpacker.getNextFormat() == MessageFormat.NIL)
                {
                    continue; // field is nullable and next value is nil so we not try to deserialize it
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("constructor", this.constructor).append("structure", this.structure).toString();
    }
}
