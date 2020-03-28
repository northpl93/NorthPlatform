package pl.north93.northplatform.minigame.elytrarace.cmd;

import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getArena;


import javax.xml.bind.JAXB;

import java.io.File;

import org.bukkit.ChatColor;

import pl.north93.northplatform.minigame.elytrarace.arena.ElytraRaceArena;
import pl.north93.northplatform.minigame.elytrarace.cfg.Boost;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;

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
        final INorthPlayer player = INorthPlayer.wrap(sender);

        final LocalArena arena = getArena(player);
        final ElytraRaceArena arenaData = arena.getArenaData();

        if (! ElytraDevMode.checkDevMode(player))
        {
            return;
        }

        if (args.length() != 2)
        {
            player.sendMessage(ChatColor.RED + "/elytrasetpower <heightPower> <speedPower> i musisz stac w booscie");
            player.sendMessage(ChatColor.RED + "Aby ustawic nulla (wylaczyc) napisz null");
            return;
        }

        for (final Boost boost : arenaData.getArenaConfig().getBoosts())
        {
            if (boost.getArea().toCuboid(player.getWorld()).contains(player.getLocation()))
            {
                if (args.asString(0).equalsIgnoreCase("null"))
                {
                    boost.setHeightPower(null);
                }
                else
                {
                    boost.setHeightPower(args.asDouble(0));
                }

                if (args.asString(1).equalsIgnoreCase("null"))
                {
                    boost.setSpeedPower(null);
                }
                else
                {
                    boost.setSpeedPower(args.asDouble(1));
                }

                player.sendMessage(ChatColor.GREEN + "Ustawiono moc na " + boost.getHeightPower() + "/" + boost.getSpeedPower());

                JAXB.marshal(arenaData.getArenaConfig(), new File(arena.getWorld().getCurrentMapTemplate().getMapDirectory(), "ElytraRaceArena.xml"));

                return;
            }
        }

        player.sendMessage(ChatColor.RED + "Musisz byc w booscie!");
    }
}
