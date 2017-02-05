package pl.north93.zgame.api.bukkit.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ItemStackBuilder
{
    private Material     material;
    private int          amount   = 1;
    private short        data     = 0;
    private String       name;
    private List<String> lore;

    public ItemStackBuilder material(final Material material)
    {
        this.material = material;
        return this;
    }

    public ItemStackBuilder amount(final int amount)
    {
        this.amount = amount;
        return this;
    }

    public ItemStackBuilder data(final short data)
    {
        this.data = data;
        return this;
    }

    public ItemStackBuilder data(final int data)
    {
        this.data = (short) data;
        return this;
    }

    public ItemStackBuilder name(final String name)
    {
        this.name = name;
        return this;
    }

    public ItemStackBuilder lore(final List<String> lore)
    {
        this.lore = lore;
        return this;
    }

    public ItemStackBuilder lore(final String... lore)
    {
        this.lore = Arrays.stream(lore).map(line -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList());
        return this;
    }

    public ItemStack build()
    {
        final ItemStack itemStack = new ItemStack(this.material, this.amount, this.data);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(this.name);
        itemMeta.setLore(this.lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("material", this.material).append("amount", this.amount).append("data", this.data).append("name", this.name).append("lore", this.lore).toString();
    }

    public static ItemStackBuilder create()
    {
        return new ItemStackBuilder();
    }
}
