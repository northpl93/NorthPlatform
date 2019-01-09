package pl.north93.northplatform.api.global.utils.lang;

import java.nio.charset.StandardCharsets;

public final class StringUtils
{
    public static byte[] toBytes(final String string)
    {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    public static String asString(final byte[] bytes)
    {
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
