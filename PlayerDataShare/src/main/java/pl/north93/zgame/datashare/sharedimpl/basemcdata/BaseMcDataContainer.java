package pl.north93.zgame.datashare.sharedimpl.basemcdata;

import pl.north93.zgame.api.global.component.annotations.SkipInjections;
import pl.north93.zgame.datashare.api.data.IDataUnit;

@SkipInjections
public class BaseMcDataContainer implements IDataUnit
{
    // inventory
    private String  inventory; // serialized inventory content
    private String  enderchest;
    private Integer heldItemSlot;
    // hp
    private Double  health;
    // food
    private Integer foodLevel;
    private Float   exhaustion;
    private Float   saturation;
    // other
    private String  statistics;
    private Integer gameMode;

    public String getInventory()
    {
        return this.inventory;
    }

    public void setInventory(final String inventory)
    {
        this.inventory = inventory;
    }

    public String getEnderchest()
    {
        return this.enderchest;
    }

    public void setEnderchest(final String enderchest)
    {
        this.enderchest = enderchest;
    }

    public Integer getHeldItemSlot()
    {
        return this.heldItemSlot;
    }

    public void setHeldItemSlot(final Integer heldItemSlot)
    {
        this.heldItemSlot = heldItemSlot;
    }

    public Double getHealth()
    {
        return this.health;
    }

    public void setHealth(final Double health)
    {
        this.health = health;
    }

    public Integer getFoodLevel()
    {
        return this.foodLevel;
    }

    public void setFoodLevel(final Integer foodLevel)
    {
        this.foodLevel = foodLevel;
    }

    public Float getExhaustion()
    {
        return this.exhaustion;
    }

    public void setExhaustion(final Float exhaustion)
    {
        this.exhaustion = exhaustion;
    }

    public Float getSaturation()
    {
        return this.saturation;
    }

    public void setSaturation(final Float saturation)
    {
        this.saturation = saturation;
    }

    public String getStatistics()
    {
        return this.statistics;
    }

    public void setStatistics(final String statistics)
    {
        this.statistics = statistics;
    }

    public Integer getGameMode()
    {
        return this.gameMode;
    }

    public void setGameMode(final Integer gameMode)
    {
        this.gameMode = gameMode;
    }
}
