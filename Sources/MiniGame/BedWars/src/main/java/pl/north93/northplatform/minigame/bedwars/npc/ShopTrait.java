package pl.north93.northplatform.minigame.bedwars.npc;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;

@TraitName("npc_shop")
public class ShopTrait extends Trait
{
    public enum NpcType
    {
        SHOP,
        UPGRADES
    }

    private final NpcType type;

    protected ShopTrait(final NpcType type)
    {
        super("npc_shop");
        this.type = type;
    }

    public NpcType getType()
    {
        return this.type;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("type", this.type).toString();
    }
}
