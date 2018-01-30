package pl.north93.zgame.api.global.messages;

import static pl.north93.zgame.api.bukkit.utils.ChatUtils.translateAlternateColorCodes;


import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

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
                    return StringUtils.split(translateAlternateColorCodes(message), '\n');
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
                    final String[] splitted = DEFAULT.processMessage(message);

                    final String[] output = new String[splitted.length + 2];
                    Arrays.fill(output, "");
                    System.arraycopy(splitted, 0, output, 1, splitted.length);

                    return output;
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
            },
    SEPARATED_CENTER
            {
                @Override
                public String[] processMessage(final String message)
                {
                    final String[] centered = CENTER.processMessage(message);

                    final String[] output = new String[centered.length + 2];
                    Arrays.fill(output, "");
                    System.arraycopy(centered, 0, output, 1, centered.length);

                    return output;
                }
            };

    public abstract String[] processMessage(String message);
}
