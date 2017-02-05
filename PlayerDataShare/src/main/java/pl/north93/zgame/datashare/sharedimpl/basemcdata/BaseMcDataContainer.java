package pl.north93.zgame.datashare.sharedimpl.basemcdata;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.SkipInjections;
import pl.north93.zgame.datashare.api.data.IDataUnit;

@SkipInjections
public class BaseMcDataContainer implements IDataUnit
{
    // inventory
    private byte[]  inventory; // serialized inventory content
    private byte[]  enderchest;
    private Integer heldItemSlot;
    // hp
    private Double  health;
    // food
    private Integer foodLevel;
    private Float   exhaustion;
    private Float   saturation;
    // other
    private byte[]  potions;
    private Integer totalExperience;
    private Integer level; // exp level
    private Double  experience;

    private String  statistics;
    private Integer gameMode;

    public byte[] getInventory()
    {
        return this.inventory;
    }

    public void setInventory(final byte[] inventory)
    {
        this.inventory = inventory;
    }

    public byte[] getEnderchest()
    {
        return this.enderchest;
    }

    public void setEnderchest(final byte[] enderchest)
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

    public byte[] getPotions()
    {
        return this.potions;
    }

    public void setPotions(final byte[] potions)
    {
        this.potions = potions;
    }

    public Integer getTotalExperience()
    {
        return this.totalExperience;
    }

    public void setTotalExperience(final Integer totalExperience)
    {
        this.totalExperience = totalExperience;
    }

    public Integer getLevel()
    {
        return this.level;
    }

    public void setLevel(final Integer level)
    {
        this.level = level;
    }

    public Double getExperience()
    {
        return this.experience;
    }

    public void setExperience(final Double experience)
    {
        this.experience = experience;
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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
