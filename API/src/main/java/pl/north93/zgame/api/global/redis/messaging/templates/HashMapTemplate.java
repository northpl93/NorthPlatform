package pl.north93.zgame.api.global.redis.messaging.templates;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import pl.north93.zgame.api.global.redis.messaging.Template;
import pl.north93.zgame.api.global.redis.messaging.TemplateGeneric;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;

public class HashMapTemplate implements TemplateGeneric<HashMap<Object, Object>>
{
    private final Class<?> keyType;
    private final Class<?> valueType;

    public HashMapTemplate(final Class<?> keyType, final Class<?> valueType)
    {
        this.keyType = keyType;
        this.valueType = valueType;
    }

    public HashMapTemplate()
    {
        this(null, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void serializeObject(final TemplateManager templateManager, final MessageBufferPacker packer, final HashMap<Object, Object> object) throws Exception
    {
        final Template<Object> keyTemplate = (Template<Object>) templateManager.getTemplate(this.keyType);
        final Template<Object> valueTemplate = (Template<Object>) templateManager.getTemplate(this.valueType);

        packer.packMapHeader(object.size());
        for (final Map.Entry<Object, Object> entry : object.entrySet())
        {
            keyTemplate.serializeObject(templateManager, packer, entry.getKey());
            valueTemplate.serializeObject(templateManager, packer, entry.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public HashMap<Object, Object> deserializeObject(final TemplateManager templateManager, final MessageUnpacker unpacker) throws Exception
    {
        final Template<Object> keyTemplate = (Template<Object>) templateManager.getTemplate(this.keyType);
        final Template<Object> valueTemplate = (Template<Object>) templateManager.getTemplate(this.valueType);

        final int mapSize = unpacker.unpackMapHeader();
        final HashMap<Object, Object> hashMap = new HashMap<>(mapSize);
        for (int i = 0; i < mapSize; i++)
        {
            final Object key = keyTemplate.deserializeObject(templateManager, unpacker);
            final Object value = valueTemplate.deserializeObject(templateManager, unpacker);
            hashMap.put(key, value);
        }

        return hashMap;
    }

    @Override
    public Template<HashMap<Object, Object>> setGenericType(final ParameterizedType type)
    {
        if (type.getActualTypeArguments().length == 2)
        {
            final Type[] args = type.getActualTypeArguments();
            return this.setGenericType((Class<?>) args[0], (Class<?>) args[1]);
        }
        throw new IllegalArgumentException();
    }

    @Override
    public Template<HashMap<Object, Object>> setGenericType(final Class<?>... genericTypes)
    {
        return new HashMapTemplate(genericTypes[0], genericTypes[1]);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("keyType", this.keyType).append("valueType", this.valueType).toString();
    }
}
