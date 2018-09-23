package pl.north93.zgame.skyplayerexp.server.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.windows.Window;

public class ShopCategories extends Window
{
    private static final ItemStack PLACEHOLDER;
    static
    {
        PLACEHOLDER = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        final ItemMeta itemMeta = PLACEHOLDER.getItemMeta();
        itemMeta.setDisplayName(" ");
        PLACEHOLDER.setItemMeta(itemMeta);
    }
    private final IServerGuiManager guiManager;
    private final Player            player;

    public ShopCategories(final IServerGuiManager guiManager, final Player player)
    {
        super("SKLEP", 3 * 9);
        this.guiManager = guiManager;
        this.player = player;
    }

    @Override
    protected void onShow()
    {
        {
            final ItemStack itemStack = new ItemStack(Material.GHAST_TEAR);
            final ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(color("&6Kupno"));
            itemMeta.setLore(lore("&7Tutaj kupisz przedmioty"));
            itemStack.setItemMeta(itemMeta);
            this.addElement(10, itemStack, event -> this.guiManager.openShopCategory(this.player, "kupno"));
        }

        {
            final ItemStack itemStack = new ItemStack(Material.IRON_INGOT, 32);
            final ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(color("&6Kupno XXL"));
            itemMeta.setLore(lore("&7Tutaj kupisz przedmioty po 32 sztuki"));
            itemStack.setItemMeta(itemMeta);
            this.addElement(11, itemStack, event -> this.guiManager.openShopCategory(this.player, "kupno_xxl"));
        }

        {
            final ItemStack itemStack = new ItemStack(Material.GOLD_NUGGET);
            final ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(color("&6Sprzedaz"));
            itemMeta.setLore(lore("&7Tutaj sprzedasz przedmioty"));
            itemStack.setItemMeta(itemMeta);
            this.addElement(13, itemStack, event -> this.guiManager.openShopCategory(this.player, "sprzedaz"));
        }

        {
            final ItemStack itemStack = new ItemStack(Material.GOLD_INGOT, 32);
            final ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(color("&6Sprzedaz XXL"));
            itemMeta.setLore(lore("&7Tutaj sprzedasz przedmioty po 32 sztuki"));
            itemStack.setItemMeta(itemMeta);
            this.addElement(14, itemStack, event -> this.guiManager.openShopCategory(this.player, "sprzedaz_xxl"));
        }

        {
            final ItemStack itemStack = new ItemStack(Material.GOLD_BLOCK);
            final ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(color("&bSklep VIP"));
            itemMeta.setLore(lore("&7Tylko dla posiadaczy rangi VIP"));
            itemStack.setItemMeta(itemMeta);
            this.addElement(16, itemStack, event -> this.guiManager.openShopCategory(this.player, "sklep_vip"));
        }

        {
            final ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
            final ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(color("&6Menu serwera"));
            itemStack.setItemMeta(itemMeta);
            this.addElement(18, itemStack, event -> this.guiManager.openServerMenu(this.player));
        }

        this.fillEmpty(PLACEHOLDER);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
