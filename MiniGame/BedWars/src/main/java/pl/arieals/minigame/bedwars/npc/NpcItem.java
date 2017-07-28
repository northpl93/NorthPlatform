package pl.arieals.minigame.bedwars.npc;

import java.util.Map;

import org.bukkit.entity.EntityType;

import pl.arieals.globalshops.shared.Item;

public class NpcItem
{
    private final Item item;
    private final int priority;
    private final EntityType entityType;

    public NpcItem(final Item item)
    {
        this.item = item;

        final Map<String, String> data = item.getData();
        this.priority = Integer.parseInt(data.get("priority"));
        this.entityType = EntityType.valueOf(data.get("type"));
    }

    public EntityType getEntityType()
    {
        return this.entityType;
    }

    public int getPriority()
    {
        return this.priority;
    }

}
