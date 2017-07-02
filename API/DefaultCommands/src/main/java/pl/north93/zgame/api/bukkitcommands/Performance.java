package pl.north93.zgame.api.bukkitcommands;

import static org.bukkit.ChatColor.YELLOW;


import java.lang.management.ManagementFactory;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;

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
        final double[] recentTps = Bukkit.getServer().getTPS();
        final Runtime runtime = Runtime.getRuntime();

        final long max = (runtime.maxMemory() / 1024 / 1024);
        final long allocated =  (runtime.totalMemory() / 1024 / 1024);
        final long free = (runtime.freeMemory() / 1024 / 1024);

        final int allocatedPercent = (int) (allocated / (double)max * 100);
        final int freePercentOfAllocated = (int) (free / (double)allocated * 100);
        final int freePercentOfMax = (int) (free / (double)max * 100);

        sender.sendRawMessage("&7Uptime: &6" + DateUtil.formatDateDiff(ManagementFactory.getRuntimeMXBean().getStartTime()));
        sender.sendRawMessage("&7TPS (1m,5m,15m): &6" + this.formatTps(recentTps[0]) + " " + this.formatTps(recentTps[1]) + " " + this.formatTps(recentTps[2]));
        sender.sendRawMessage("&7Max memory: &6{0}MB", max);
        sender.sendRawMessage("&7Allocated memory: &6{0}MB &7(&6{1}% &7of max)", allocated, allocatedPercent);
        sender.sendRawMessage("&7Free memory: &6{0}MB &7(&6{1}% &7of allocated, &6{2}% &7of max)", free, freePercentOfAllocated, freePercentOfMax);
        sender.sendRawMessage("&7Worlds:");

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

            sender.sendRawMessage(" &7T:&6" + worldType + " &7N:&6" + w.getName() + " &7Chunks:&6" + w.getLoadedChunks().length + " &7Entities:&6" + w.getEntities().size() + " &7Tiles:&6" + tileEntities);
        }
    }

    private String formatTps(final double tps)
    {
        return (tps > 18.0D? ChatColor.GREEN:(tps > 16.0D? YELLOW:ChatColor.RED)) + (tps > 20.0D?"*":"") + Math.min((double)Math.round(tps * 100.0D) / 100.0D, 20.0D);
    }
}
