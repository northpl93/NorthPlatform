package pl.arieals.minigame.elytrarace.cmd;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;
import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.elytrarace.arena.ElytraRaceArena;
import pl.arieals.minigame.elytrarace.arena.ElytraRacePlayer;
import pl.arieals.minigame.elytrarace.cfg.Boost;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;

public class ElytraSetPower extends NorthCommand
{
    public ElytraSetPower()
    {
        super("elytrasetpower");
        this.setPermission("dev");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();
        final LocalArena arena = getArena(player);
        final ElytraRaceArena arenaData = arena.getArenaData();

        final ElytraRacePlayer playerData = getPlayerData(player, ElytraRacePlayer.class);
        if (playerData == null || ! playerData.isDev())
        {
            player.sendMessage(ChatColor.RED + "Musisz byc w devmode! Wpisz /elytradevmode");
            return;
        }

        if (args.length() != 2)
        {
            player.sendMessage(ChatColor.RED + "/elytrasetpower <heightPower> <speedPower> i musisz stac w booscie");
            return;
        }

        for (final Boost boost : arenaData.getArenaConfig().getBoosts())
        {
            if (boost.getArea().toCuboid(player.getWorld()).contains(player.getLocation()))
            {
                boost.setHeightPower(args.asDouble(0));
                boost.setSpeedPower(args.asDouble(1));
                player.sendMessage(ChatColor.GREEN + "Ustawiono moc na " + boost.getHeightPower() + "/" + boost.getSpeedPower());
                return;
            }
        }

        player.sendMessage(ChatColor.RED + "Musisz byc w booscie!");
    }
}
