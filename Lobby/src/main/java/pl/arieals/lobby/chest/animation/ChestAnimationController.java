package pl.arieals.lobby.chest.animation;

import javax.annotation.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.server.v1_10_R1.EntityArmorStand;
import net.minecraft.server.v1_10_R1.MinecraftServer;
import net.minecraft.server.v1_10_R1.WorldServer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.entityhider.EntityVisibility;
import pl.north93.zgame.api.bukkit.entityhider.IEntityHider;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;

public class ChestAnimationController
{
    private final IEntityHider           entityHider;
    private final Set<AnimationInstance> animations = new HashSet<>();

    @Bean
    private ChestAnimationController(final BukkitApiCore apiCore, final IEntityHider entityHider)
    {
        this.entityHider = entityHider;
        // uruchamiamy watek animacji
        new ChestAnimationThread().start();
        apiCore.registerEvents(new AnimationListener(this));
    }

    public void createAnimation(final Player player, final Location location)
    {
        synchronized (this.animations)
        {
            final AnimationInstance animationInstance = this.create(player, location);
            this.animations.add(animationInstance);
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
     *
     * @param entity
     * @return
     */
    public @Nullable AnimationInstance getInstanceByEntity(final Entity entity)
    {
        for (final AnimationInstance animation : this.animations)
        {
            if (animation.getArmorStand() == entity)
            {
                return animation;
            }
        }

        return null;
    }

    // tworzy nowa instancje animacji widoczna dla danego
    // gracza
    private AnimationInstance create(final Player player, final Location location)
    {
        final CraftWorld craftWorld = (CraftWorld) location.getWorld();
        final WorldServer worldServer = craftWorld.getHandle();

        final EntityArmorStand entityArmorStand = new EntityArmorStand(worldServer);
        final ArmorStand armorStand = (ArmorStand) entityArmorStand.getBukkitEntity();
        this.setupVisibility(player, armorStand); // konfigurujemy widocznosc tego entity

        armorStand.setVisible(false);
        armorStand.setAI(false);
        armorStand.setGravity(false);
        armorStand.setHelmet(new ItemStack(Material.CHEST));

        entityArmorStand.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        worldServer.addEntity(entityArmorStand, CreatureSpawnEvent.SpawnReason.CUSTOM);

        final AnimationInstance animationInstance = new AnimationInstance(player, location, armorStand);
        animationInstance.setAnimation(new ChestSpawnAnimation(animationInstance));

        return animationInstance;
    }

    private void setupVisibility(final Player player, final Entity entity)
    {
        final Set<Entity> entities = Collections.singleton(entity);
        this.entityHider.setVisibility(EntityVisibility.HIDDEN, entities);
        this.entityHider.setVisibility(player, EntityVisibility.VISIBLE, entities);
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
                }

                try
                {
                    synchronized (this)
                    {
                        this.wait(10);
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

