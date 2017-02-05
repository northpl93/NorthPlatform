package pl.north93.zgame.skyplayerexp.bungee.tablist;

import static net.md_5.bungee.api.ChatColor.translateAlternateColorCodes;
import static net.md_5.bungee.api.chat.TextComponent.fromLegacyText;


import net.md_5.bungee.chat.ComponentSerializer;

public final class Utils
{
    private Utils()
    {
    }

    public static String packetJson(final String legacyText)
    {
        return ComponentSerializer.toString(fromLegacyText(translateAlternateColorCodes('&', legacyText)));
    }
}
