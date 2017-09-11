package pl.north93.zgame.api.bukkit.entityhider.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.entityhider.IEntityHider;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class EntityHiderImpl extends Component implements IEntityHider
{
    @Inject
    private BukkitApiCore apiCore;

    @Override
    public void showEntities(final Player player, final List<Entity> entities)
    {
        final Set<Integer> hiddenEntities = this.getHiddenEntities(player);
        hiddenEntities.removeAll(entities.stream().map(Entity::getEntityId).collect(Collectors.toSet()));
        /*for (final Entity entity : entities) // gdy to odkomentujemy to nie ma opoznienia, ale to chyba noe przeszkadza
        {
            final CraftEntity craftEntity = (CraftEntity) entity;
            final EntityTrackerEntry tracker = EntityTrackerHelper.getTrackerEntry(craftEntity.getHandle());
            tracker.updatePlayer(((CraftPlayer) player).getHandle());
        }*/
    }

    @Override
    public void hideEntities(final Player player, final List<Entity> entities)
    {
        final Set<Integer> hiddenEntities = this.getHiddenEntities(player);
        hiddenEntities.addAll(entities.stream().map(Entity::getEntityId).collect(Collectors.toSet()));
        /*for (final Entity entity : entities)
        {
            final CraftEntity craftEntity = (CraftEntity) entity;
            final EntityTrackerEntry tracker = EntityTrackerHelper.getTrackerEntry(craftEntity.getHandle());
            tracker.updatePlayer(((CraftPlayer) player).getHandle());
        }*/
    }

    @Override
    public void setEntityVisible(final Player player, final Entity entity, final boolean visible)
    {
        final Set<Integer> hiddenEntities = this.getHiddenEntities(player);

        if (visible)
        {
            hiddenEntities.remove(entity.getEntityId());
        }
        else
        {
            hiddenEntities.add(entity.getEntityId());
        }
    }

    @Override
    public boolean isVisible(final Player player, final Entity entity)
    {
        return ! this.getHiddenEntities(player).contains(entity.getEntityId());
    }

    @SuppressWarnings("unchecked")
    private Set<Integer> getHiddenEntities(final Player player)
    {
        final List<MetadataValue> metadata = player.getMetadata("API.EntityHider/hiddenEntities");
        if (metadata.size() == 0)
        {
            final Set<Integer> entities = new HashSet<>();
            player.setMetadata("API.EntityHider/hiddenEntities", new FixedMetadataValue(this.apiCore.getPluginMain(), entities));
            return entities;
        }
        return (Set<Integer>) metadata.get(0).value();
    }

    @Override
    protected void enableComponent()
    {
        try
        {
            EntityTrackerEntryPatcher.applyChange(this.apiCore.getInstrumentationClient());
        }
        catch (final Exception e)
        {
            this.apiCore.getLogger().log(Level.SEVERE, "Failed to apply EntityHider patch.", e);
        }
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
