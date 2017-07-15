package pl.north93.zgame.api.bukkit.gui;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import pl.north93.zgame.api.bukkit.utils.itemstack.ItemStackBuilder;
import pl.north93.zgame.api.global.messages.TranslatableString;
import pl.north93.zgame.api.global.utils.Vars;

public class GuiIcon
{
    private final Material type;
    private final int data;
    private final int count;
    
    private final TranslatableString label;
    private final TranslatableString lore;
    
    private final boolean glowing;
    
    private GuiIcon(Material type, int data, int count, TranslatableString label, TranslatableString lore, boolean glow)
    {
        this.type = type;
        this.data = data;
        this.count = count;
        this.label = label;
        this.lore = lore;
        this.glowing = glow;
    }
    
    public Material getType()
    {
        return type;
    }
    
    public int getData()
    {
        return data;
    }
    
    public int getCount()
    {
        return count;
    }
    
    public TranslatableString getLabel()
    {
        return label;
    }
    
    public TranslatableString getLore()
    {
        return lore;
    }
    
    public boolean isGlowing()
    {
        return glowing;
    }
    
    public ItemStack toItemStack(Player player, Vars<String> parameters)
    {
        List<String> lore = Arrays.asList(this.lore.getValue(player.spigot().getLocale(), parameters).split("\n"));
        return new ItemStackBuilder().material(type).data(data).amount(count).name(label.getValue(player.spigot().getLocale(), parameters))
                .lore(lore).build();
    }
    
    public static Builder builder()
    {
        return new Builder();
    }
    
    public static class Builder
    {
        private Material type = Material.DIRT;
        private int data = 0;
        private int count = 1;
        
        private TranslatableString label = TranslatableString.of("");
        private TranslatableString lore = TranslatableString.of("");
        
        private boolean glowing;
        
        private Builder()
        {
        }
        
        public Builder type(Material type)
        {
            this.type = type;
            return this;
        }
        
        public Builder data(int data)
        {
            this.data = data;
            return this;
        }
        
        public Builder count(int count)
        {
            this.count = count;
            return this;
        }
        
        public Builder label(TranslatableString label)
        {
            this.label = label;
            return this;
        }
        
        public Builder lore(TranslatableString lore)
        {
            this.lore = lore;
            return this;
        }
        
        public Builder glowing(boolean glowing)
        {
            this.glowing = glowing;
            return this;
        }
        
        public GuiIcon build()
        {
            return new GuiIcon(type, data, count, label, lore, glowing);
        }
    }
}
