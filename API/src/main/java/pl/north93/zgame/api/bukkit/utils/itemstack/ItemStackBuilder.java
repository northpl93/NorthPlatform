package pl.north93.zgame.api.bukkit.utils.itemstack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.utils.xml.XmlEnchant;
import pl.north93.zgame.api.global.messages.LegacyMessage;

public class ItemStackBuilder
{
    private ItemStack        itemStack;
    
    private Material         material;
    private int              amount   = 1;
    private short            data     = 0;
    private String           name;
    private List<String>     lore;
    private List<XmlEnchant> enchantments;
    private ItemFlag[]       flags;
    private boolean          unbreakable;

    public ItemStackBuilder()
    {
    }
    
    private ItemStackBuilder(ItemStack wrappedStack)
    {
        itemStack = wrappedStack;
        material = itemStack.getType();
        amount = itemStack.getAmount();
        data = itemStack.getDurability();
    }
    
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

    public ItemStackBuilder lore(final String lore)
    {
        return lore(LegacyMessage.fromString(lore));
    }
    
    public ItemStackBuilder lore(final LegacyMessage lore)
    {
        return lore(lore.asList());
    }
    
    public ItemStackBuilder lore(final List<String> lore)
    {
        this.lore = lore;
        return this;
    }

    public ItemStackBuilder lore(final String... lore)
    {
        this.lore = Arrays.stream(lore).flatMap(line -> Arrays.stream(line.split("\n"))).map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.toList());
        return this;
    }

    public ItemStackBuilder enchant(final XmlEnchant enchantment)
    {
        if (this.enchantments == null)
        {
            this.enchantments = new ArrayList<>();
        }
        this.enchantments.add(enchantment);
        return this;
    }

    public ItemStackBuilder enchant(final Enchantment enchantment, final int level)
    {
        return this.enchant(new XmlEnchant(enchantment, level));
    }

    public ItemStackBuilder hideAttributes()
    {
        flags = new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE };
        return this;
    }
    
    public ItemStackBuilder flags(ItemFlag... flags)
    {
        this.flags = flags;
        return this;
    }
    
    public ItemStackBuilder unbreakable()
    {
        this.unbreakable = true;
        return this;
    }

    public ItemStack build()
    {
        final ItemStack itemStack;
        if ( this.itemStack == null )
        {
            itemStack = new ItemStack(this.material, this.amount, this.data);
        }
        else
        {
            itemStack = this.itemStack;
            itemStack.setType(this.material);
            itemStack.setAmount(this.amount);
            itemStack.setDurability(this.data);
        }
        
        if (this.material == Material.AIR)
        {
            // w powietrzu nic wiecej nie ustawimy...
            return itemStack;
        }

        final ItemMeta itemMeta = itemStack.getItemMeta();
        
        if ( this.name != null )
        {
            itemMeta.setDisplayName(this.name);
        }
        
        if ( this.lore != null )
        {
            itemMeta.setLore(this.lore);
        }
        
        if (this.flags != null)
        {
            itemMeta.addItemFlags(flags);
        }
        if ( this.unbreakable )
        {
            itemMeta.setUnbreakable(true);
        }
        itemStack.setItemMeta(itemMeta);
        if (this.enchantments != null)
        {
            itemMeta.getEnchants().keySet().forEach(itemStack::removeEnchantment);
            this.enchantments.forEach(enchantment -> enchantment.apply(itemStack));
        }
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
    
    public static ItemStackBuilder wrap(ItemStack itemStack)
    {
        return new ItemStackBuilder(itemStack);
    }
}
