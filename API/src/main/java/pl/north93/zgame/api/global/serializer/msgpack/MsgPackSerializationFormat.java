package pl.north93.zgame.api.global.serializer.msgpack;

import java.util.ArrayList;
import java.util.UUID;

import pl.north93.zgame.api.global.serializer.msgpack.template.MsgPackArrayListTemplate;
import pl.north93.zgame.api.global.serializer.msgpack.template.MsgPackUuidTemplate;
import pl.north93.zgame.api.global.serializer.platform.SerializationFormat;
import pl.north93.zgame.api.global.serializer.platform.context.DeserializationContext;
import pl.north93.zgame.api.global.serializer.platform.context.SerializationContext;
import pl.north93.zgame.api.global.serializer.platform.template.ExactTypeIgnoreGenericFilter;
import pl.north93.zgame.api.global.serializer.platform.template.TemplateEngine;

public class MsgPackSerializationFormat implements SerializationFormat<byte[]>
{
    @Override
    public void configure(final TemplateEngine templateEngine)
    {
        templateEngine.register(new ExactTypeIgnoreGenericFilter(UUID.class), new MsgPackUuidTemplate());

        templateEngine.register(new ExactTypeIgnoreGenericFilter(ArrayList.class), new MsgPackArrayListTemplate());
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
