package pl.north93.zgame.api.global.redis.messaging.templates;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import pl.north93.zgame.api.global.redis.messaging.Template;
import pl.north93.zgame.api.global.redis.messaging.TemplateGeneric;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;

public class ArrayListTemplate implements TemplateGeneric<ArrayList<Object>>
{
    private final Class<?> genericType;

    public ArrayListTemplate()
    {
        this(null);
    }

    public ArrayListTemplate(final Class<?> genericType)
    {
        this.genericType = genericType;
    }

    @Override
    public Template<ArrayList<Object>> setGenericType(final ParameterizedType type)
    {
        if (type.getActualTypeArguments().length == 1)
        {
            return this.setGenericType((Class<?>) type.getActualTypeArguments()[0]);
        }
        throw new IllegalArgumentException();
    }

    @Override
    public Template<ArrayList<Object>> setGenericType(final Class<?>... genericTypes)
    {
        return new ArrayListTemplate(genericTypes[0]);
    }

    @Override
    public void serializeObject(final TemplateManager templateManager, final MessageBufferPacker packer, final ArrayList object) throws Exception
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
    public ArrayList<Object> deserializeObject(final TemplateManager templateManager, final MessageUnpacker unpacker) throws Exception
    {
        final Template template = templateManager.getTemplate(this.genericType);
        final int elements = unpacker.unpackArrayHeader();

        final ArrayList<Object> list = new ArrayList<>(elements);
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
