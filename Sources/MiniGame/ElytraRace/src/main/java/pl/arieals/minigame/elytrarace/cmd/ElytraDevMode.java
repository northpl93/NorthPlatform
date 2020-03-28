package pl.arieals.minigame.elytrarace.cmd;

import org.bukkit.ChatColor;

import pl.arieals.minigame.elytrarace.arena.ElytraRacePlayer;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
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
        final INorthPlayer player = INorthPlayer.wrap(sender);
        final ElytraRacePlayer playerData = player.getPlayerData(ElytraRacePlayer.class);

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

    public static boolean checkDevMode(final INorthPlayer player)
    {
        final ElytraRacePlayer playerData = player.getPlayerData(ElytraRacePlayer.class);
        if (playerData == null || ! playerData.isDev())
        {
            player.sendMessage(ChatColor.RED + "Musisz byc w devmode! Wpisz /elytradevmode");
            return false;
        }
        return true;
    }
}
