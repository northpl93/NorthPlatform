package pl.north93.northplatform.api.bukkit.entityhider.impl;

import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.entity.Entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.entityhider.EntityVisibility;

class GlobalVisibility
{
    private final Map<Entity, EntityVisibility> visibilityMap = new WeakHashMap<>(); // weak hashmapa zeby nie powodowac memory leak.

    public void setGlobalEntityStatus(final Entity entity, final EntityVisibility visibility)
    {
        // zeby nie syfic w mapie
        if (visibility == EntityVisibility.NEUTRAL)
        {
            this.visibilityMap.remove(entity);
        }
        else
        {
            this.visibilityMap.put(entity, visibility);
        }
    }

    public EntityVisibility getGlobalVisibility(final Entity entity)
    {
        return this.visibilityMap.getOrDefault(entity, EntityVisibility.NEUTRAL);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("visibilityMap", this.visibilityMap).toString();
    }
}
