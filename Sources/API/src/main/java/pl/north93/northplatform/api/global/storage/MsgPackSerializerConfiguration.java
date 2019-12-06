package pl.north93.northplatform.api.global.storage;

import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.serializer.msgpack.MsgPackSerializationFormat;
import pl.north93.serializer.platform.NorthSerializer;
import pl.north93.serializer.platform.template.impl.NorthSerializerImpl;

public class MsgPackSerializerConfiguration
{
    @Bean
    private MsgPackSerializerConfiguration()
    {
    }

    @Bean
    public NorthSerializer<byte[], byte[]> msgPackSerializer()
    {
        return new NorthSerializerImpl<>(new MsgPackSerializationFormat(), new NorthPlatformClassResolver());
    }
}
