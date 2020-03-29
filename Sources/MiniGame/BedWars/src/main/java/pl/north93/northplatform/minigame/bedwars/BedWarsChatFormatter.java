package pl.north93.northplatform.minigame.bedwars;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.utils.chat.ChatUtils;
import pl.north93.northplatform.api.chat.global.ChatFormatter;
import pl.north93.northplatform.api.chat.global.formatter.PermissionsBasedFormatter;
import pl.north93.northplatform.minigame.bedwars.arena.BedWarsPlayer;

public class BedWarsChatFormatter implements ChatFormatter
{
    public static final BedWarsChatFormatter INSTANCE = new BedWarsChatFormatter();

    @Override
    public BaseComponent format(final INorthPlayer player, final String input)
    {
        final BaseComponent baseFormat = PermissionsBasedFormatter.INSTANCE.format(player, input);

        final BedWarsPlayer playerData = player.getPlayerData(BedWarsPlayer.class);
        if (playerData == null || playerData.getTeam() == null)
        {
            // występuje gdy jesteśmy na etapie lobby
            return baseFormat;
        }

        final BaseComponent prefix = ChatUtils.fromLegacyText(playerData.getTeam().getColor() + "■ ");

        return new TextComponent(prefix, baseFormat);
    }
}
