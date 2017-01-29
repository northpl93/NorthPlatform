package pl.north93.zgame.skyblock.shop.cfg;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.defaults.CfgIntDefault;
import org.diorite.cfg.annotations.defaults.CfgShortDefault;

public class BukkitItem
{
    private Material     material;
    @CfgIntDefault(1)
    private int          amount;
    @CfgShortDefault(0)
    private short        data;
    private String       name;
    private List<String> lore;

    public Material getMaterial()
    {
        return this.material;
    }

    public int getAmount()
    {
        return this.amount;
    }

    public short getData()
    {
        return this.data;
    }

    public String getName()
    {
        return this.name;
    }

    public List<String> getLore()
    {
        return this.lore;
    }

    public ItemStack asBukkit()
    {
        final ItemStack itemStack = new ItemStack(this.material, this.amount, this.data);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (this.name != null)
        {
            itemMeta.setDisplayName(this.name);
        }
        if (this.lore != null && !this.lore.isEmpty())
        {
            itemMeta.setLore(this.lore);
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("material", this.material).append("amount", this.amount).append("data", this.data).append("name", this.name).append("lore", this.lore).toString();
    }
}
