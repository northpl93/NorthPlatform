package pl.north93.northplatform.api.bukkit.entityhider.impl;


import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.EntityTrackerEntry;
import net.minecraft.server.v1_12_R1.World;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.entityhider.EntityVisibility;
import pl.north93.northplatform.api.bukkit.entityhider.IEntityHider;
import pl.north93.northplatform.api.bukkit.server.IBukkitServerManager;
import pl.north93.northplatform.api.bukkit.utils.nms.EntityTrackerHelper;
import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

public class EntityHiderImpl extends Component implements IEntityHider
{
    @Inject
    private IBukkitServerManager serverManager;
    private final GlobalVisibility globalVisibility = new GlobalVisibility();

    @Override
    public void setVisibility(final Player player, final EntityVisibility visibility, final Collection<Entity> entities)
    {
        final VisibilityController controller = this.getController(player);
        entities.forEach(entity -> controller.setVisibility(entity, visibility));
        this.refreshEntities(entities);
    }

    @Override
    public void setVisibility(final EntityVisibility visibility, final Collection<Entity> entities)
    {
        entities.forEach(entity -> this.globalVisibility.setGlobalEntityStatus(entity, visibility));
        this.refreshEntities(entities);
    }

    @Override
    public boolean isVisible(final Player player, final Entity entity)
    {
        return this.getController(player).isEntityVisible(entity);
    }

    @Override
    public void refreshEntities(final Collection<Entity> entities)
    {
        for (final Entity entity : entities)
        {
            final EntityTrackerEntry tracker = EntityTrackerHelper.getTrackerEntry(EntityTrackerHelper.toNmsEntity(entity));
            if (tracker == null)
            {
                continue;
            }

            final World nmsWorld = tracker.b().world;
            for (final EntityHuman possibleHumanObserver : new HashSet<>(nmsWorld.players)) // todo is copying needed?
            {
                final EntityPlayer playerObserver = (EntityPlayer) possibleHumanObserver;

                final VisibilityController controller = this.getController(playerObserver.getBukkitEntity());
                if (controller.isEntityVisible(entity))
                {
                    // podany gracz powinien widziec to entity, wiec prosimy Minecrafta o sprawdzenie czy
                    // trzeba rozpoczac trackowanie
                    tracker.updatePlayer(playerObserver);
                }
                else
                {
                    // podany gracz ma nie widziec tego entity wiec kasujemy go z listy sledzacych
                    tracker.clear(playerObserver);
                }
            }
        }
    }

    private VisibilityController getController(final Player player)
    {
        final List<MetadataValue> metadata = player.getMetadata("API.EntityHider/controller");
        if (metadata.isEmpty())
        {
            final VisibilityController controller = new VisibilityController(this.globalVisibility);
            player.setMetadata("API.EntityHider/controller", this.serverManager.createFixedMetadataValue(controller));

            return controller;
        }
        return (VisibilityController) metadata.get(0).value();
    }

    @Override
    protected void enableComponent()
    {
    }

    @Override
    protected void disableComponent()
    {
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
