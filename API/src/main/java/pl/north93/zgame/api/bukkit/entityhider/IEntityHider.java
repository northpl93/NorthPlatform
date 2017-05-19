package pl.north93.zgame.api.bukkit.entityhider;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface IEntityHider
{
    void showEntities(Player player, List<Entity> entities);

    void hideEntities(Player player, List<Entity> entities);

    void setEntityVisible(Player player, Entity entity, boolean visible);

    boolean isVisible(Player player, Entity entity);
}
