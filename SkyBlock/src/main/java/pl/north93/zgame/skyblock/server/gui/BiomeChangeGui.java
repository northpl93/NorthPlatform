package pl.north93.zgame.skyblock.server.gui;

import java.util.function.Consumer;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.utils.ItemStackBuilder;
import pl.north93.zgame.api.bukkit.windows.Window;
import pl.north93.zgame.skyblock.shared.api.NorthBiome;

public class BiomeChangeGui extends Window
{
    private static final ItemStack PLACEHOLDER = ItemStackBuilder.create().material(Material.STAINED_GLASS_PANE).data(15).name(" ").build();
    private final Consumer<NorthBiome> callback;

    public BiomeChangeGui(final Consumer<NorthBiome> callback)
    {
        super("Wybierz biom", 3 * 9);
        this.callback = callback;
    }

    @Override
    protected void onShow()
    {
        {
            final ItemStack grass = ItemStackBuilder.create().material(Material.GRASS).name(ChatColor.DARK_GREEN + "Domyslny").build();
            this.addElement(10, grass, event -> this.callback.accept(NorthBiome.OVERWORLD));
        }

        {
            final ItemStack netherRack = new ItemStack(Material.NETHERRACK);
            final ItemMeta itemMeta = netherRack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.RED + "Nether");
            netherRack.setItemMeta(itemMeta);
            this.addElement(12, netherRack, event -> this.callback.accept(NorthBiome.NETHER));
        }

        {
            final ItemStack end = new ItemStack(Material.ENDER_STONE);
            final ItemMeta itemMeta = end.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GRAY + "The End");
            end.setItemMeta(itemMeta);
            this.addElement(14, end, event -> this.callback.accept(NorthBiome.THE_END));
        }

        this.fillEmpty(PLACEHOLDER);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
