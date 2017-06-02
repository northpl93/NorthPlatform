package pl.arieals.minigame.elytrarace.cmd;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;
import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import javax.xml.bind.JAXB;

import java.io.File;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.elytrarace.BoostType;
import pl.arieals.minigame.elytrarace.arena.ElytraRaceArena;
import pl.arieals.minigame.elytrarace.arena.ElytraRacePlayer;
import pl.arieals.minigame.elytrarace.cfg.Boost;
import pl.north93.zgame.api.bukkit.utils.xml.XmlCuboid;
import pl.north93.zgame.api.bukkit.utils.xml.XmlLocation;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;

public class ElytraAddBoost extends NorthCommand
{
    public ElytraAddBoost()
    {
        super("elytraaddboost", "elytraddboost");
        this.setPermission("dev");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();
        final ElytraRacePlayer playerData = getPlayerData(player, ElytraRacePlayer.class);
        if (playerData == null || ! playerData.isDev())
        {
            player.sendMessage(ChatColor.RED + "Musisz byc w devmode! Wpisz /elytradevmode");
            return;
        }

        if (args.length() != 2)
        {
            player.sendMessage(ChatColor.RED + "/elytraaddboost <boosterType> <power>");
            return;
        }

        final WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        final Selection selection = worldEditPlugin.getSelection(player);

        if (selection == null)
        {
            player.sendMessage(ChatColor.RED + "Zaznacz teren boostera worldeditem.");
            return;
        }

        final XmlLocation xmlLoc1 = new XmlLocation(selection.getMinimumPoint());
        final XmlLocation xmlLoc2 = new XmlLocation(selection.getMaximumPoint());
        final XmlCuboid cuboid = new XmlCuboid(xmlLoc1, xmlLoc2);

        final Boost boost = new Boost(cuboid, args.asEnumValue(BoostType.class, 0), args.asDouble(1));

        final LocalArena arena = getArena(player);
        final ElytraRaceArena arenaData = arena.getArenaData();
        arenaData.getArenaConfig().getBoosts().add(boost);

        player.sendMessage(ChatColor.GREEN + "Dodano nowy booster! typ:" + boost.getBoostType() + " moc:" + boost.getBoostPower());
        player.sendMessage(ChatColor.GREEN + "lokacja1: " + xmlLoc1);
        player.sendMessage(ChatColor.GREEN + "lokacja2: " + xmlLoc2);
        player.sendMessage(" ");

        JAXB.marshal(arenaData.getArenaConfig(), new File(arena.getWorld().getCurrentMapTemplate().getMapDirectory(), "ElytraRaceArena.xml"));
    }
}
