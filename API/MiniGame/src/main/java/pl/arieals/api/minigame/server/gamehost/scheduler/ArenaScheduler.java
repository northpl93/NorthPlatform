package pl.arieals.api.minigame.server.gamehost.scheduler;

import static net.minecraft.server.v1_10_R1.MinecraftServer.currentTick;


import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_10_R1.scheduler.CraftScheduler;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.utils.reflections.DioriteReflectionUtils;
import org.diorite.utils.reflections.MethodInvoker;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.Main;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class ArenaScheduler implements IArenaScheduler
{
    private static final MethodInvoker parsePending = DioriteReflectionUtils.getMethod(CraftScheduler.class, "parsePending");
    @Inject
    private BukkitApiCore                 apiCore;
    private final LocalArena              arena;
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
        final Main pluginMain = this.apiCore.getPluginMain();
        this.wrappers.add(new WrappedBukkitTask(Bukkit.getScheduler().runTaskLater(pluginMain, task, delay)));
    }

    @Override
    public void runTaskTimer(final Runnable task, final long delay, final long every)
    {
        final Main pluginMain = this.apiCore.getPluginMain();
        this.wrappers.add(new WrappedBukkitTask(Bukkit.getScheduler().runTaskTimer(pluginMain, task, delay, every)));
    }

    @Override
    public void cancelAndClear()
    {
        this.wrappers.forEach(BukkitTaskWrapper::cancel);
        this.wrappers.clear();
    }

    public void moveTimeForward(final long tickDiff)
    {
        for (final BukkitTaskWrapper wrapper : this.wrappers)
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
                if (wrapper.getNextRun() >= time)
                {
                    wrapper.run();
                    wrapper.cancel();
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
