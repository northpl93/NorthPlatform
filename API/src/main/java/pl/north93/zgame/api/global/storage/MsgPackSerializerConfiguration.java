package pl.north93.zgame.api.global.storage;

import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.serializer.msgpack.MsgPackSerializationFormat;
import pl.north93.zgame.api.global.serializer.platform.NorthSerializer;
import pl.north93.zgame.api.global.serializer.platform.impl.NorthSerializerImpl;

public class MsgPackSerializerConfiguration
{
    @Bean
    private MsgPackSerializerConfiguration()
    {
    }

    @Bean
    public NorthSerializer<byte[]> msgPackSerializer()
    {
        return new NorthSerializerImpl<>(new MsgPackSerializationFormat(), new NorthPlatformClassResolver());
    }
}
