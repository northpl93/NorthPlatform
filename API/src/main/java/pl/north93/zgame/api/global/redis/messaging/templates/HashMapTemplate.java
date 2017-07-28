package pl.north93.zgame.api.global.redis.messaging.templates;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.tuple.Pair;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import pl.north93.zgame.api.global.redis.messaging.Template;
import pl.north93.zgame.api.global.redis.messaging.TemplateGeneric;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;

public class HashMapTemplate implements TemplateGeneric<HashMap<Object, Object>>
{
    private final Class<Object> keyType;
    private final Class<Object> valueType;

    public HashMapTemplate(final Class<Object> keyType, final Class<Object> valueType)
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
        final Pair<Template<Object>, Template<Object>> templates = this.getTemplates(templateManager);

        packer.packMapHeader(object.size());
        for (final Map.Entry<Object, Object> entry : object.entrySet())
        {
            templates.getKey().serializeObject(templateManager, packer, entry.getKey());
            templates.getValue().serializeObject(templateManager, packer, entry.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public HashMap<Object, Object> deserializeObject(final TemplateManager templateManager, final MessageUnpacker unpacker) throws Exception
    {
        final Pair<Template<Object>, Template<Object>> templates = this.getTemplates(templateManager);

        final int mapSize = unpacker.unpackMapHeader();
        final HashMap<Object, Object> hashMap = new HashMap<>(mapSize);
        for (int i = 0; i < mapSize; i++)
        {
            final Object key = templates.getKey().deserializeObject(templateManager, unpacker);
            final Object value = templates.getValue().deserializeObject(templateManager, unpacker);
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

    @SuppressWarnings("unchecked")
    @Override
    public Template<HashMap<Object, Object>> setGenericType(final Class<?>... genericTypes)
    {
        return new HashMapTemplate((Class) genericTypes[0], (Class) genericTypes[1]);
    }

    private Pair<Template<Object>, Template<Object>> getTemplates(final TemplateManager templates)
    {
        final Template<Object> keyTemplate = templates.getTemplate(this.keyType == null ? Object.class : this.keyType);
        final Template<Object> valueTemplate = templates.getTemplate(this.valueType == null ? Object.class : this.valueType);

        return Pair.of(keyTemplate, valueTemplate);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("keyType", this.keyType).append("valueType", this.valueType).toString();
    }
}
