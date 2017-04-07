package pl.north93.zgame.api.global.data;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import com.lambdaworks.redis.codec.RedisCodec;

public class StringByteRedisCodec implements RedisCodec<String, byte[]>
{
    public static final StringByteRedisCodec INSTANCE = new StringByteRedisCodec();

    private StringByteRedisCodec()
    {
    }

    @Override
    public String decodeKey(final ByteBuffer byteBuffer)
    {
        return new String(this.decodeValue(byteBuffer), StandardCharsets.UTF_8);
    }

    @Override
    public byte[] decodeValue(final ByteBuffer byteBuffer)
    {
        final byte[] result = new byte[byteBuffer.remaining()];
        byteBuffer.get(result);
        return result;
    }

    @Override
    public ByteBuffer encodeKey(final String string)
    {
        return ByteBuffer.wrap(string.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public ByteBuffer encodeValue(final byte[] bytes)
    {
        return ByteBuffer.wrap(bytes);
    }
}
