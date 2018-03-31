package pl.north93.zgame.api.chat.global;

import net.md_5.bungee.api.chat.BaseComponent;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;

public interface ChatFormatter
{
    BaseComponent format(INorthPlayer player, String input);
}
