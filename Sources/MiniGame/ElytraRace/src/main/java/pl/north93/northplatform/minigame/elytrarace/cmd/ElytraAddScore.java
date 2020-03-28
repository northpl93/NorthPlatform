package pl.north93.northplatform.minigame.elytrarace.cmd;

import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getArena;


import javax.xml.bind.JAXB;

import java.io.File;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import pl.north93.northplatform.minigame.elytrarace.arena.ElytraRaceArena;
import pl.north93.northplatform.minigame.elytrarace.cfg.Score;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.utils.xml.XmlCuboid;
import pl.north93.northplatform.api.bukkit.utils.xml.XmlLocation;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;

public class ElytraAddScore extends NorthCommand
{
    public ElytraAddScore()
    {
        super("elytraaddscore", "elytraddscore");
        this.setPermission("dev");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final INorthPlayer player = INorthPlayer.wrap(sender);
        if (! ElytraDevMode.checkDevMode(player))
        {
            return;
        }

        if (args.length() < 1 || args.length() > 2)
        {
            player.sendMessage(ChatColor.RED + "/elytraaddscore <scoreGroup> [schieveGroup]");
            return;
        }

        final WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        final Selection selection = worldEditPlugin.getSelection(player);

        if (selection == null)
        {
            player.sendMessage(ChatColor.RED + "Zaznacz teren score ringu worldeditem.");
            return;
        }

        final XmlLocation xmlLoc1 = new XmlLocation(selection.getMinimumPoint());
        final XmlLocation xmlLoc2 = new XmlLocation(selection.getMaximumPoint());
        final XmlCuboid cuboid = new XmlCuboid(xmlLoc1, xmlLoc2);

        final String achieveGroup = args.length() == 2 ? args.asString(1) : null;

        final Score score = new Score(cuboid, achieveGroup, args.asString(0));

        final LocalArena arena = getArena(player);
        final ElytraRaceArena arenaData = arena.getArenaData();
        arenaData.getArenaConfig().getScores().add(score);

        player.sendMessage(ChatColor.GREEN + "Dodano nowy score! achieveGroup:" + achieveGroup + " scoreGroup:" + args.asString(0));
        player.sendMessage(ChatColor.GREEN + "lokacja1: " + xmlLoc1);
        player.sendMessage(ChatColor.GREEN + "lokacja2: " + xmlLoc2);
        player.sendMessage(" ");

        JAXB.marshal(arenaData.getArenaConfig(), new File(arena.getWorld().getCurrentMapTemplate().getMapDirectory(), "ElytraRaceArena.xml"));
    }
}
