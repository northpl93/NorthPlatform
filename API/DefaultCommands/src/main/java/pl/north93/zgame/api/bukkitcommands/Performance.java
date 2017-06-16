package pl.north93.zgame.api.bukkitcommands;

import java.lang.management.ManagementFactory;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_10_R1.CraftServer;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.utils.DateUtil;

@SuppressWarnings("MagicNumber")
public class Performance extends NorthCommand
{
    public Performance()
    {
        super("performance", "gc");
        this.setPermission("api.command.performance");
        this.setAsync(true); // causes tick drop
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final double[] recentTps = ((CraftServer) Bukkit.getServer()).getServer().recentTps;

        sender.sendRawMessage(ChatColor.YELLOW + "Uptime: " + DateUtil.formatDateDiff(ManagementFactory.getRuntimeMXBean().getStartTime()));
        sender.sendRawMessage(ChatColor.YELLOW + "TPS (1m,5m,15m): " + this.formatTps(recentTps[0]) + " " + this.formatTps(recentTps[1]) + " " + this.formatTps(recentTps[2]));
        sender.sendRawMessage(ChatColor.YELLOW + "Max memory: " + (Runtime.getRuntime().maxMemory() / 1024 / 1024));
        sender.sendRawMessage(ChatColor.YELLOW + "Total memory: " + (Runtime.getRuntime().totalMemory() / 1024 / 1024));
        sender.sendRawMessage(ChatColor.YELLOW + "Free memory: " + (Runtime.getRuntime().freeMemory() / 1024 / 1024));
        sender.sendRawMessage(ChatColor.YELLOW + "Worlds:");

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

            sender.sendRawMessage(ChatColor.YELLOW + " T:" + worldType + " N:" + w.getName() + " LoadedChunks:" + w.getLoadedChunks().length + " Entities:" + w.getEntities().size() + " TileEntities:" + tileEntities);
        }
    }

    private String formatTps(final double tps)
    {
        return (tps > 18.0D? ChatColor.GREEN:(tps > 16.0D?ChatColor.YELLOW:ChatColor.RED)) + (tps > 20.0D?"*":"") + Math.min((double)Math.round(tps * 100.0D) / 100.0D, 20.0D);
    }
}
