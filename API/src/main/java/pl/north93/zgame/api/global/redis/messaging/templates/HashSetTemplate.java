package pl.north93.zgame.api.global.redis.messaging.templates;

import java.lang.reflect.ParameterizedType;
import java.util.HashSet;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import pl.north93.zgame.api.global.redis.messaging.Template;
import pl.north93.zgame.api.global.redis.messaging.TemplateGeneric;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;

public class HashSetTemplate implements TemplateGeneric<HashSet<Object>>
{
    private final Class<Object> genericType;

    public HashSetTemplate()
    {
        this(null);
    }

    public HashSetTemplate(final Class<Object> genericType)
    {
        this.genericType = genericType;
    }

    @Override
    public Template<HashSet<Object>> setGenericType(final ParameterizedType type)
    {
        if (type.getActualTypeArguments().length == 1)
        {
            return this.setGenericType((Class<?>) type.getActualTypeArguments()[0]);
        }
        throw new IllegalArgumentException();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Template<HashSet<Object>> setGenericType(final Class<?>... genericTypes)
    {
        return new HashSetTemplate((Class<Object>) genericTypes[0]);
    }

    @Override
    public void serializeObject(final TemplateManager templateManager, final MessageBufferPacker packer, final HashSet<Object> object) throws Exception
    {
        final Template<Object> template = templateManager.getTemplate(this.genericType);

        packer.packArrayHeader(object.size());
        for (final Object element : object)
        {
            template.serializeObject(templateManager, packer, element);
        }
    }

    @Override
    public HashSet<Object> deserializeObject(final TemplateManager templateManager, final MessageUnpacker unpacker) throws Exception
    {
        final Template template = templateManager.getTemplate(this.genericType);
        final int elements = unpacker.unpackArrayHeader();

        final HashSet<Object> set = new HashSet<>(elements);
        if (elements == 0)
        {
            return set;
        }

        for (int i = 0; i < elements; i++)
        {
            set.add(template.deserializeObject(templateManager, unpacker));
        }
        return set;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("genericType", this.genericType).toString();
    }
}
