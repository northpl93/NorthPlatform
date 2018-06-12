package pl.north93.zgame.features.bukkit.chat.admin;

import net.md_5.bungee.api.chat.BaseComponent;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.bukkit.utils.chat.ChatUtils;
import pl.north93.zgame.api.chat.global.ChatFormatter;

public class AdminChatFormatter implements ChatFormatter
{
    @Override
    public BaseComponent format(final INorthPlayer player, final String input)
    {
        return ChatUtils.parseLegacyText("&c[AC] {0}: &f{1}", player.getName(), input);
    }
}
