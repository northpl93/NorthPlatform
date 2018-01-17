package pl.arieals.lobby.chest.animation;

import javax.annotation.Nullable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.server.v1_12_R1.MinecraftServer;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.hologui.IIcon;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;

public final class ChestAnimationController
{
    private final ChestAnimationThread   animationThread;
    private final Set<AnimationInstance> animations = new HashSet<>();

    @Bean
    private ChestAnimationController()
    {
        // uruchamiamy watek animacji
        this.animationThread = new ChestAnimationThread();
        this.animationThread.start();
    }

    public void createAnimation(final Player player, final IIcon icon)
    {
        synchronized (this.animations)
        {
            final AnimationInstance animationInstance = this.create(player, icon);
            this.animations.add(animationInstance);
        }

        synchronized (this.animationThread)
        {
            this.animationThread.notify(); // wzbudzamy watek animacji
        }
    }

    public void destroyAnimation(final Player player)
    {
        synchronized (this.animations)
        {
            final Iterator<AnimationInstance> iterator = this.animations.iterator();
            while (iterator.hasNext())
            {
                final AnimationInstance animation = iterator.next();
                if (animation.getOwner() != player)
                {
                    continue;
                }

                animation.setDestroyed();
                iterator.remove();
            }
        }
    }

    /**
     * Zwraca instancje animacji na podstawie ikony do ktorej
     * jest przypisana animacja.
     *
     * @param icon Ikona do sprawdzenia.
     * @return Instancja animacji jesli podana ikona do niej nalezy.
     */
    public @Nullable AnimationInstance getInstanceByIcon(final IIcon icon)
    {
        for (final AnimationInstance animation : this.animations)
        {
            if (animation.getIcon() == icon)
            {
                return animation;
            }
        }

        return null;
    }

    // tworzy nowa instancje animacji na podstawie ikony
    private AnimationInstance create(final Player player, final IIcon icon)
    {
        final AnimationInstance animationInstance = new AnimationInstance(player, icon);
        animationInstance.setAnimation(new ChestSpawnAnimation(animationInstance));

        return animationInstance;
    }

    private class ChestAnimationThread extends Thread
    {
        private ChestAnimationThread()
        {
            this.setDaemon(true);
            this.setPriority(Thread.MIN_PRIORITY);
        }

        @Override
        public void run()
        {
            while (MinecraftServer.getServer().isRunning()) // dopki serwer pracuje
            {
                final ChestAnimationController controller = ChestAnimationController.this;
                final boolean shouldRun;

                synchronized (controller.animations)
                {
                    final Iterator<AnimationInstance> iterator = controller.animations.iterator();
                    while (iterator.hasNext())
                    {
                        final AnimationInstance instance = iterator.next();
                        if (instance.isDestroyed())
                        {
                            iterator.remove();
                            continue;
                        }

                        instance.tick();
                    }

                    shouldRun = ! controller.animations.isEmpty();
                }

                try
                {
                    synchronized (this)
                    {
                        if (shouldRun)
                        {
                            this.wait(10);
                        }
                        else
                        {
                            this.wait(); // blokujemy watek zeby nie uzywac cpu
                        }
                    }
                }
                catch (final InterruptedException e)
                {
                    // ignore interruption
                }
            }
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("animations", this.animations).toString();
    }
}

