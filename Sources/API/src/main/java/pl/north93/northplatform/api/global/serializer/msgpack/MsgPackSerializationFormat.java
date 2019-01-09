package pl.north93.northplatform.api.global.serializer.msgpack;

import javax.annotation.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import pl.north93.northplatform.api.global.serializer.msgpack.template.MsgPackListTemplate;
import pl.north93.northplatform.api.global.serializer.platform.SerializationFormat;
import pl.north93.northplatform.api.global.serializer.platform.TypePredictor;
import pl.north93.northplatform.api.global.serializer.platform.template.ExactTypeIgnoreGenericFilter;
import pl.north93.northplatform.api.global.serializer.platform.template.TemplateEngine;
import pl.north93.northplatform.api.global.serializer.msgpack.template.MsgPackArrayTemplate;
import pl.north93.northplatform.api.global.serializer.msgpack.template.MsgPackMapTemplate;
import pl.north93.northplatform.api.global.serializer.msgpack.template.MsgPackSetTemplate;
import pl.north93.northplatform.api.global.serializer.msgpack.template.MsgPackUuidTemplate;
import pl.north93.northplatform.api.global.serializer.platform.template.AnyInheritedTypeFilter;

public class MsgPackSerializationFormat implements SerializationFormat<byte[], MsgPackSerializationContext, MsgPackDeserializationContext>
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
    public MsgPackSerializationContext createSerializationContext(final TemplateEngine templateEngine)
    {
        return new MsgPackSerializationContext(templateEngine);
    }

    @Override
    public MsgPackDeserializationContext createDeserializationContext(final TemplateEngine templateEngine, final byte[] serializedData)
    {
        return new MsgPackDeserializationContext(templateEngine, serializedData);
    }

    @Nullable
    @Override
    public TypePredictor<MsgPackSerializationContext, MsgPackDeserializationContext> getTypePredictor()
    {
        return null;
    }
}
