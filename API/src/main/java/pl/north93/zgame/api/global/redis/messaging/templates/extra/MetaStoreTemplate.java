package pl.north93.zgame.api.global.redis.messaging.templates.extra;

import java.util.Map;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.metadata.MetaStore;
import pl.north93.zgame.api.global.redis.messaging.Template;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;

public class MetaStoreTemplate implements Template<MetaStore>
{
    @Override
    public void serializeObject(final TemplateManager templateManager, final MessageBufferPacker packer, final MetaStore object) throws Exception
    {
        final Template<Object> dynamicObjectTemplate = templateManager.getTemplate(Object.class);
        final Map<MetaKey, Object> internalMap = object.getInternalMap();
        packer.packMapHeader(internalMap.size());
        for (final Map.Entry<MetaKey, Object> entry : internalMap.entrySet())
        {
            packer.packString(entry.getKey().getKey());
            packer.packBoolean(entry.getKey().isPersist());
            dynamicObjectTemplate.serializeObject(templateManager, packer, entry.getValue());
        }
    }

    @Override
    public MetaStore deserializeObject(final TemplateManager templateManager, final MessageUnpacker unpacker) throws Exception
    {
        final Template<Object> dynamicObjectTemplate = templateManager.getTemplate(Object.class);
        final MetaStore metaStore = new MetaStore();
        final Map<MetaKey, Object> internalMap = metaStore.getInternalMap();
        final int size = unpacker.unpackMapHeader();

        for (int i = 0; i < size; i++)
        {
            final MetaKey metaKey = MetaKey.get(unpacker.unpackString(), unpacker.unpackBoolean());
            internalMap.put(metaKey, dynamicObjectTemplate.deserializeObject(templateManager, unpacker));
        }

        return metaStore;
    }
}
