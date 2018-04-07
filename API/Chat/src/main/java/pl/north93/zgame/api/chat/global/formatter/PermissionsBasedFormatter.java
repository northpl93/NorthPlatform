package pl.north93.zgame.api.chat.global.formatter;

import net.md_5.bungee.api.chat.BaseComponent;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.bukkit.utils.chat.ChatUtils;
import pl.north93.zgame.api.chat.global.ChatFormatter;
import pl.north93.zgame.api.global.permissions.Group;

/**
 * Podstawowy formatter który używa formatu skonfigurowanego w systemie uprawnień.
 */
public class PermissionsBasedFormatter implements ChatFormatter
{
    public static final ChatFormatter INSTANCE = new PermissionsBasedFormatter();

    private PermissionsBasedFormatter()
    {
    }

    @Override
    public BaseComponent format(final INorthPlayer player, final String input)
    {
        final Group group = player.getGroup();

        final String result = String.format(group.getChatFormat(), player.getDisplayName(), input);
        return ChatUtils.fromLegacyText(result);
    }
}
