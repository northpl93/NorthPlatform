package pl.north93.zgame.api.bukkit.cmd;

import static pl.north93.zgame.api.global.I18n.getBukkitMessage;


import java.lang.management.ManagementFactory;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_10_R1.CraftServer;

import pl.north93.zgame.api.global.utils.DateUtil;

@SuppressWarnings("MagicNumber")
public class Performance implements CommandExecutor
{
    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String s, final String[] args)
    {
        if (!sender.hasPermission("api.command.performance") && !sender.isOp())
        {
            sender.sendMessage(getBukkitMessage("command.no_permissions"));
            return true;
        }

        final double[] recentTps = ((CraftServer) Bukkit.getServer()).getServer().recentTps;

        sender.sendMessage(ChatColor.YELLOW + "Uptime: " + DateUtil.formatDateDiff(ManagementFactory.getRuntimeMXBean().getStartTime()));
        sender.sendMessage(ChatColor.YELLOW + "TPS (1m,5m,15m): " + this.formatTps(recentTps[0]) + " " + this.formatTps(recentTps[1]) + " " + this.formatTps(recentTps[2]));
        sender.sendMessage(ChatColor.YELLOW + "Max memory: " + (Runtime.getRuntime().maxMemory() / 1024 / 1024));
        sender.sendMessage(ChatColor.YELLOW + "Total memory: " + (Runtime.getRuntime().totalMemory() / 1024 / 1024));
        sender.sendMessage(ChatColor.YELLOW + "Free memory: " + (Runtime.getRuntime().freeMemory() / 1024 / 1024));
        sender.sendMessage(ChatColor.YELLOW + "Worlds:");

        final List<World> worlds = Bukkit.getWorlds();
        for (final World w : worlds)
        {
            String worldType = "World";
            switch (w.getEnvironment())
            {
                case NETHER:
                    worldType = "Nether";
                    break;
                case THE_END:
                    worldType = "The End";
                    break;
            }

            int tileEntities = 0;

            try
            {
                for (final Chunk chunk : w.getLoadedChunks())
                {
                    tileEntities += chunk.getTileEntities().length;
                }
            }
            catch (final ClassCastException ex)
            {
                ex.printStackTrace();
            }

            sender.sendMessage(ChatColor.YELLOW + " T:" + worldType + " N:" + w.getName() + " LoadedChunks:" + w.getLoadedChunks().length + " Entities:" + w.getEntities().size() + " TileEntities:" + tileEntities);
        }

        return true;
    }

    private String formatTps(final double tps)
    {
        return (tps > 18.0D? ChatColor.GREEN:(tps > 16.0D?ChatColor.YELLOW:ChatColor.RED)) + (tps > 20.0D?"*":"") + Math.min((double)Math.round(tps * 100.0D) / 100.0D, 20.0D);
    }
}
