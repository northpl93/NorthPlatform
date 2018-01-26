package pl.north93.zgame.api.global.messages;

import java.util.Locale;

/**
 * Prosty enum ulatwiajacy odmiane przez liczby.
 */
public enum PluralForm
{
    ONE,
    SOME,
    MANY;

    public String getName()
    {
        return this.name().toLowerCase(Locale.ROOT);
    }

    /**
     * Aby uzyc tej metody klucz musi miec postac: costam.ONE/costam.SOME/costam.MANY
     * Jako klucz podajemy wtedy costam, a num jako nasza liczbe.
     * Dostajemy klucz z doklejona wlasciwa forma.
     *
     * @param key nazwa klucza bez kropki na koncu.
     * @param num ilosc przez ktora odmieniamy.
     * @return klucz we wlasciwej formie.
     */
    public static String transformKey(final String key, final int num)
    {
        return key + "." + get(num).getName();
    }

    public static PluralForm get(final int num)
    {
        if (num == 1)
        {
            return ONE;
        }

        final int last = num > 20 ? num % 10 : num;
        if (last == 2 || last == 3 || last == 4)
        {
            return SOME;
        }

        return MANY;
    }
}
