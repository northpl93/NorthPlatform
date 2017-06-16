package pl.north93.zgame.api.global.messages;

import static pl.north93.zgame.api.bukkit.utils.ChatUtils.translateAlternateColorCodes;


import org.apache.commons.lang.StringUtils;

import pl.north93.zgame.api.bukkit.utils.ChatUtils;

public enum MessageLayout
{
    DEFAULT
            {
                @Override
                public String[] processMessage(final String message)
                {
                    return new String[] { translateAlternateColorCodes(message) };
                }
            },
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
