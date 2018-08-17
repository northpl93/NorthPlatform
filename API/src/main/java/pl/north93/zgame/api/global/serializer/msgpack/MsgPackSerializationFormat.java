package pl.north93.zgame.api.global.serializer.msgpack;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import pl.north93.zgame.api.global.serializer.msgpack.template.MsgPackArrayTemplate;
import pl.north93.zgame.api.global.serializer.msgpack.template.MsgPackListTemplate;
import pl.north93.zgame.api.global.serializer.msgpack.template.MsgPackMapTemplate;
import pl.north93.zgame.api.global.serializer.msgpack.template.MsgPackSetTemplate;
import pl.north93.zgame.api.global.serializer.msgpack.template.MsgPackUuidTemplate;
import pl.north93.zgame.api.global.serializer.platform.SerializationFormat;
import pl.north93.zgame.api.global.serializer.platform.context.DeserializationContext;
import pl.north93.zgame.api.global.serializer.platform.context.SerializationContext;
import pl.north93.zgame.api.global.serializer.platform.template.AnyInheritedTypeFilter;
import pl.north93.zgame.api.global.serializer.platform.template.ExactTypeIgnoreGenericFilter;
import pl.north93.zgame.api.global.serializer.platform.template.TemplateEngine;

public class MsgPackSerializationFormat implements SerializationFormat<byte[]>
{
    @Override
    public void configure(final TemplateEngine templateEngine)
    {
        templateEngine.register(new MsgPackArrayTemplate.ArrayTemplateFilter(), new MsgPackArrayTemplate());

        // zwykle typy
        templateEngine.register(new ExactTypeIgnoreGenericFilter(UUID.class), new MsgPackUuidTemplate());

        // kolekcje
        templateEngine.register(new AnyInheritedTypeFilter(List.class), new MsgPackListTemplate());
        templateEngine.register(new AnyInheritedTypeFilter(Set.class), new MsgPackSetTemplate());

        // mapy
        templateEngine.register(new AnyInheritedTypeFilter(Map.class), new MsgPackMapTemplate());
    }

    @Override
    public SerializationContext createSerializationContext(final TemplateEngine templateEngine)
    {
        return new MsgPackSerializationContext(templateEngine);
    }

    @Override
    public DeserializationContext createDeserializationContext(final TemplateEngine templateEngine, final byte[] serializedData)
    {
        return new MsgPackDeserializationContext(templateEngine, serializedData);
    }
}
