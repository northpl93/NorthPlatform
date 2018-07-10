package pl.north93.zgame.api.bukkit.entityhider.impl;

import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.entity.Entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.entityhider.EntityVisibility;

/**
 * Kontroler pilnujacy widocznosci entities przez gracza z nim powiazanego.
 */
class VisibilityController
{
    private final Map<Entity, EntityVisibility> visibilityMap = new WeakHashMap<>(0);
    private final GlobalVisibility globalVisibility;

    VisibilityController(final GlobalVisibility globalVisibility)
    {
        this.globalVisibility = globalVisibility;
    }

    private EntityVisibility getPlayerVisibility(final Entity entity)
    {
        return this.visibilityMap.getOrDefault(entity, EntityVisibility.NEUTRAL);
    }

    /**
     * Sprawdza widocznosc danego entity dla gracza.
     * Uwzglednia preferencje ustawione dla tego gracza i globalne.
     *
     * @param entity Entity do sprawdzenia.
     * @return Aktualny status widocznosci danego entity.
     */
    EntityVisibility getVisibility(final Entity entity)
    {
        final EntityVisibility globalVisibility = this.globalVisibility.getGlobalVisibility(entity);
        final EntityVisibility playerVisibility = this.getPlayerVisibility(entity);

        return globalVisibility.and(playerVisibility);
    }

    /**
     * Sprawdza czy ten gracz moze zobaczyc entity wedlug ustawionych
     * regul dla gracza i regul globalnych.
     *
     * @param entity Entity do sprawdzenia.
     * @return Czy gracz moze zobaczyc entity.
     */
    boolean isEntityVisible(final Entity entity)
    {
        return this.getVisibility(entity).isVisible();
    }

    void setVisibility(final Entity entity, final EntityVisibility visibility)
    {
        if (visibility == EntityVisibility.NEUTRAL)
        {
            this.visibilityMap.remove(entity);
        }
        else
        {
            this.visibilityMap.put(entity, visibility);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("visibilityMap", this.visibilityMap).append("globalVisibility", this.globalVisibility).toString();
    }
}
