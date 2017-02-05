package pl.north93.zgame.api.bukkit.permissions;

public final class FastToLowerCase
{
    private static final char[] LOWER_CASE =
            {
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, '\'',
                    '(', ')', '*', '+', ',', '-', '.', '/',
                    '0', '1', '2', '3', '4', '5', '6', '7',
                    '8', '9', 0, 0, 0, 0, 0, 0,
                    0, 'a', 'b', 'c', 'd', 'e', 'f', 'g',
                    'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
                    'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                    'x', 'y', 'z', 0, 0, 0, 0, 0,
                    0, 'a', 'b', 'c', 'd', 'e', 'f', 'g',
                    'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
                    'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                    'x', 'y', 'z', 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0
            };

    /**
     * Rewrote the toLowercase method to improve performances.
     *
     * @param value The String to lowercase
     *
     * @return The lowercase string
     */
    public static String toLowerCase(final String value)
    {
        final char[] chars = value.toCharArray();

        for (int i = 0; i < chars.length; i++)
        {
            chars[i] = LOWER_CASE[chars[i]];
        }

        return new String(chars);
    }
}
