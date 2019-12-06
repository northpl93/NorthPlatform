package pl.arieals.minigame.elytrarace.cmd;

import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getArena;
import static pl.arieals.minigame.elytrarace.cmd.ElytraDevMode.checkDevMode;


import javax.xml.bind.JAXB;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.elytrarace.arena.ElytraRaceArena;
import pl.arieals.minigame.elytrarace.cfg.Boost;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;

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

        if (! checkDevMode(player))
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
