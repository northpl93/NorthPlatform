package pl.arieals.minigame.elytrarace.cmd;

import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import pl.arieals.minigame.elytrarace.arena.ElytraRacePlayer;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;

public class ElytraDevMode extends NorthCommand
{
    public ElytraDevMode()
    {
        super("elytradevmode");
        this.setPermission("dev");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();
        final ElytraRacePlayer playerData = getPlayerData(player, ElytraRacePlayer.class);

        if (playerData == null)
        {
            player.sendMessage(ChatColor.RED + "Brak ElytraRacePlayer, tej komendy mozesz uzyc tylko po starcie gry.");
            return;
        }

        if (playerData.isDev())
        {
            player.sendMessage(ChatColor.RED + "Wylaczono tryb dev dla " + player.getDisplayName());
            playerData.setDev(false);
        }
        else
        {
            player.sendMessage(ChatColor.RED + "Wlaczono tryb dev dla " + player.getDisplayName());
            playerData.setDev(true);
        }
    }

    public static boolean checkDevMode(final Player player)
    {
        final ElytraRacePlayer playerData = getPlayerData(player, ElytraRacePlayer.class);
        if (playerData == null || ! playerData.isDev())
        {
            player.sendMessage(ChatColor.RED + "Musisz byc w devmode! Wpisz /elytradevmode");
            return false;
        }
        return true;
    }
}
