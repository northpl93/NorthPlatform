package pl.arieals.minigame.bedwars;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.bukkit.utils.chat.ChatUtils;
import pl.north93.zgame.api.chat.global.ChatFormatter;
import pl.north93.zgame.api.chat.global.formatter.PermissionsBasedFormatter;

public class BedWarsChatFormatter implements ChatFormatter
{
    public static final BedWarsChatFormatter INSTANCE = new BedWarsChatFormatter();

    @Override
    public BaseComponent format(final INorthPlayer player, final String input)
    {
        final BaseComponent baseFormat = PermissionsBasedFormatter.INSTANCE.format(player, input);

        final BedWarsPlayer playerData = getPlayerData(player, BedWarsPlayer.class);
        if (playerData == null)
        {
            // występuje gdy jesteśmy na etapie lobby
            return baseFormat;
        }

        final BaseComponent prefix = ChatUtils.fromLegacyText(playerData.getTeam().getColor() + "■ ");

        return new TextComponent(prefix, baseFormat);
    }
}
