package pl.north93.zgame.api.global.messages;

import static pl.north93.zgame.api.bukkit.utils.ChatUtils.translateAlternateColorCodes;


import org.apache.commons.lang.StringUtils;

import pl.north93.zgame.api.bukkit.utils.ChatUtils;

/**
 * Klasa odpowiedzialna za formatowanie wiadomości.
 */
public enum MessageLayout
{
    /**
     * Domyślny format, tylko koloruje wiadomość.
     */
    DEFAULT
            {
                @Override
                public String[] processMessage(final String message)
                {
                    return new String[] { translateAlternateColorCodes(message) };
                }
            },
    /**
     * Wiadomość otaczają spacje linijkę wyżej i niżej.
     */
    SEPARATED
            {
                @Override
                public String[] processMessage(final String message)
                {
                    return new String[] {"", translateAlternateColorCodes(message), ""};
                }
            },
    /**
     * Wiadomość jest wyśrodkowana sprytnym algorytmem.
     */
    CENTER
            {
                @Override
                public String[] processMessage(final String message)
                {
                    final String[] split = StringUtils.split(message, '\n');
                    if (split.length == 1)
                    {
                        return new String[] { ChatUtils.centerMessage(message) };
                    }
                    for (int i = 0; i < split.length; i++)
                    {
                        split[i] = ChatUtils.centerMessage(split[i]);
                    }
                    return split;
                }
            };

    public abstract String[] processMessage(String message);
}
