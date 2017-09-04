package pl.north93.zgame.api.global.redis.messaging.templates;

import java.lang.reflect.ParameterizedType;
import java.util.LinkedList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import pl.north93.zgame.api.global.redis.messaging.Template;
import pl.north93.zgame.api.global.redis.messaging.TemplateGeneric;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;

public class LinkedListTemplate implements TemplateGeneric<LinkedList<Object>>
{
    private final Class<?> genericType;

    public LinkedListTemplate()
    {
        // generic template will be optionally set later by setGenericType.
        // By default we use dynamic template.
        this(null);
    }

    public LinkedListTemplate(final Class<?> genericType)
    {
        // if we receive null generic type we will use dynamic template (Object.class) to prevent NPE.
        this.genericType = genericType != null ? genericType : Object.class;
    }

    @Override
    public Template<LinkedList<Object>> setGenericType(final ParameterizedType type)
    {
        if (type.getActualTypeArguments().length == 1)
        {
            return this.setGenericType((Class<?>) type.getActualTypeArguments()[0]);
        }
        throw new IllegalArgumentException();
    }

    @Override
    public Template<LinkedList<Object>> setGenericType(final Class<?>... genericTypes)
    {
        return new LinkedListTemplate(genericTypes[0]);
    }

    @Override
    public void serializeObject(final TemplateManager templateManager, final MessageBufferPacker packer, final LinkedList object) throws Exception
    {
        @SuppressWarnings("unchecked")
        final Template<Object> template = (Template<Object>) templateManager.getTemplate(this.genericType);

        packer.packArrayHeader(object.size());
        for (final Object element : object)
        {
            template.serializeObject(templateManager, packer, element);
        }
    }

    @Override
    public LinkedList<Object> deserializeObject(final TemplateManager templateManager, final MessageUnpacker unpacker) throws Exception
    {
        final Template template = templateManager.getTemplate(this.genericType);
        final int elements = unpacker.unpackArrayHeader();

        final LinkedList<Object> list = new LinkedList<>();
        if (elements == 0)
        {
            return list;
        }

        for (int i = 0; i < elements; i++)
        {
            list.add(template.deserializeObject(templateManager, unpacker));
        }
        return list;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("genericType", this.genericType).toString();
    }
}
