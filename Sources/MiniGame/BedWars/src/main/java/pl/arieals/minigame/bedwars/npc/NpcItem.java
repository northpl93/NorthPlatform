package pl.arieals.minigame.bedwars.npc;

import java.util.Map;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import pl.arieals.globalshops.server.domain.Item;

public class NpcItem
{
    private final Item item;
    private final int priority;
    private final EntityType entityType;
    private final String profileData;
    private final String dataSign;
    private final Villager.Profession profession;

    public NpcItem(final Item item)
    {
        this.item = item;

        final Map<String, String> data = item.getData();
        this.priority = Integer.parseInt(data.get("priority"));
        this.entityType = EntityType.valueOf(data.get("type"));

        if (this.entityType == EntityType.PLAYER)
        {
            this.profileData = data.get("profile");
            this.dataSign = data.get("sign");
        }
        else
        {
            this.profileData = null;
            this.dataSign = null;
        }

        this.profession = Villager.Profession.valueOf(data.getOrDefault("profession", "NORMAL"));
    }

    public Item getItem()
    {
        return this.item;
    }

    public EntityType getEntityType()
    {
        return this.entityType;
    }

    public int getPriority()
    {
        return this.priority;
    }

    public String getProfileData()
    {
        return this.profileData;
    }

    public String getDataSign()
    {
        return this.dataSign;
    }

    public Villager.Profession getVillagerProfession()
    {
        return this.profession;
    }
}
