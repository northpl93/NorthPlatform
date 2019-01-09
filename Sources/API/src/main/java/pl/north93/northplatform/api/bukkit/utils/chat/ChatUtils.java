package pl.north93.northplatform.api.bukkit.utils.chat;

import java.util.regex.Pattern;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

public final class ChatUtils
{
    public  static final char    COLOR_CHAR          = '§';
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)(" + COLOR_CHAR + "|&)[0-9A-FK-OR]");

    /**
     * Konwertuje tekst legacy na BaseComponent, bez systemu parametrów.
     * Kody kolorów są tłumaczone w tej metodzie, nie ma potrzeby robienia tego ręcznie.
     *
     * @param legacyText Tekst do skonwertowania.
     * @return Wynikowy komponent.
     */
    public static BaseComponent fromLegacyText(final String legacyText)
    {
        final BaseComponent[] components = LegacyTextConverter.fromLegacyText(legacyText);
        if (components.length == 1)
        {
            return components[0];
        }

        return new TextComponent(components);
    }

    /**
     * Tworzy nową instancję {@link ComponentBuilder} z podanym tekstem legacy.
     *
     * @param legacyText Tekst legacy do dodania na początek buildera.
     * @return Builder z poprawnie wczytanym tekstem legacy.
     */
    public static ComponentBuilder builderFromLegacyText(final String legacyText)
    {
        final BaseComponent[] components = LegacyTextConverter.fromLegacyText(legacyText);
        return new ComponentBuilder("").append(components);
    }

    /**
     * Parsuje dany legacy tekst wraz z parametrami do BaseComponent.
     * Kody kolorów są tłumaczone w tej metodzie, nie ma potrzeby robienia tego ręcznie.
     * Więcej w dokumentacji klasy {@link LegacyTextParser}.
     *
     * @param legacyText Tekst legacy do sparsowania.
     * @return Wynikowy komponent parsowania.
     */
    public static BaseComponent parseLegacyText(final String legacyText, final Object... params)
    {
        return LegacyTextParser.parseLegacyText(legacyText, params);
    }

    public static String stripColor(final String input)
    {
        return input == null ? null : STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

    public static String translateAlternateColorCodes(final String textToTranslate)
    {
        final char[] b = textToTranslate.toCharArray();

        for (int i = 0; i < b.length - 1; ++ i)
        {
            if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > - 1)
            {
                b[i] = 167;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }

        return new String(b);
    }
}
