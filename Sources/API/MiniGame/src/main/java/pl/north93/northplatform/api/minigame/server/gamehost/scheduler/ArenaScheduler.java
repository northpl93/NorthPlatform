package pl.north93.northplatform.api.minigame.server.gamehost.scheduler;

import static net.minecraft.server.v1_12_R1.MinecraftServer.currentTick;


import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.scheduler.CraftScheduler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.commons.reflections.DioriteReflectionUtils;
import org.diorite.commons.reflections.MethodInvoker;

import pl.north93.northplatform.api.bukkit.server.IBukkitServerManager;
import pl.north93.northplatform.api.bukkit.utils.AbstractCountdown;
import pl.north93.northplatform.api.bukkit.utils.SimpleCountdown;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;

public class ArenaScheduler implements IArenaScheduler
{
    private static final MethodInvoker parsePending = DioriteReflectionUtils.getMethod(CraftScheduler.class, "parsePending");
    @Inject
    private IBukkitServerManager serverManager;
    private final LocalArena arena;
    private final List<BukkitTaskWrapper> wrappers;

    public ArenaScheduler(final LocalArena arena)
    {
        this.arena = arena;
        this.wrappers = new ArrayList<>();
    }

    @Override
    public LocalArena getArena()
    {
        return this.arena;
    }

    @Override
    public void runTaskLater(final Runnable task, final long delay)
    {
        final JavaPlugin plugin = this.serverManager.getPlugin();
        this.wrappers.add(new WrappedBukkitTask(Bukkit.getScheduler().runTaskLater(plugin, task, delay)));
    }

    @Override
    public void runTaskTimer(final Runnable task, final long delay, final long every)
    {
        final JavaPlugin plugin = this.serverManager.getPlugin();
        this.wrappers.add(new WrappedBukkitTask(Bukkit.getScheduler().runTaskTimer(plugin, task, delay, every)));
    }

    @Override
    public void runAbstractCountdown(final AbstractCountdown countdown, final long every)
    {
        final JavaPlugin plugin = this.serverManager.getPlugin();
        this.wrappers.add(new WrappedBukkitTask(countdown.runTaskTimer(plugin, 0, every)));
    }

    @Override
    public void runSimpleCountdown(final SimpleCountdown countdown)
    {
        countdown.start();
        final BukkitTask task = countdown.getTask();

        this.wrappers.add(new WrappedBukkitTask(task));
    }

    @Override
    public void cancelAndClear()
    {
        this.wrappers.forEach(BukkitTaskWrapper::cancel);
        this.wrappers.clear();
    }

    public void moveTimeForward(final long tickDiff)
    {
        for (final BukkitTaskWrapper wrapper : new ArrayList<>(this.wrappers))
        {
            if (wrapper.isCancelled())
            {
                continue;
            }

            long time = currentTick + tickDiff;
            if (wrapper.isRepeated())
            {
                while ((time -= wrapper.getPeriod()) > currentTick)
                {
                    wrapper.run();
                }
                wrapper.setNextRun(time);
            }
            else
            {
                if (time >= wrapper.getNextRun())
                {
                    wrapper.run();
                    wrapper.cancel();
                }
                else
                {
                    wrapper.setNextRun(wrapper.getNextRun() - tickDiff);
                }
            }
        }
        parsePending.invoke(Bukkit.getScheduler()); // rebuild queue
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("wrappers", this.wrappers).toString();
    }
}
