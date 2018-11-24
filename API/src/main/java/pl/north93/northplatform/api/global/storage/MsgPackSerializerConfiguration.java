package pl.north93.northplatform.api.global.storage;

import pl.north93.northplatform.api.global.serializer.msgpack.MsgPackSerializationFormat;
import pl.north93.northplatform.api.global.serializer.platform.NorthSerializer;
import pl.north93.northplatform.api.global.serializer.platform.impl.NorthSerializerImpl;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;

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
