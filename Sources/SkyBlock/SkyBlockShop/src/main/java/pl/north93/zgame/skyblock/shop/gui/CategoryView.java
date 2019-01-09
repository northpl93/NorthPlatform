package pl.north93.zgame.skyblock.shop.gui;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.tuple.Pair;

import org.diorite.utils.lazy.LazyValue;

import pl.north93.zgame.api.bukkit.windows.ClickHandler;
import pl.north93.zgame.api.bukkit.windows.PaginateWindow;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.skyblock.shop.ShopComponent;
import pl.north93.zgame.skyblock.shop.api.ICategory;
import pl.north93.zgame.skyblock.shop.api.IShopEntry;

public class CategoryView extends PaginateWindow<IShopEntry>
{
    @Inject
    private       ShopComponent      shopComponent;
    private final ICategory          category;
    private final boolean            showBackToCategories;
    private final Consumer<Player>   categoriesAction;
    private final LazyValue<Boolean> hasPermission = new LazyValue<>(this::checkPermission);

    public CategoryView(final ICategory category, final boolean showBackToCategories, final Consumer<Player> categoriesAction, final Collection<IShopEntry> entries)
    {
        super(entries, category.getDisplayName(), 5, 1);
        this.category = category;
        this.showBackToCategories = showBackToCategories;
        this.categoriesAction = categoriesAction;
    }

    @Override
    protected void drawNavigator(final int offset)
    {
        if (this.hasPreviousPage())
        {
            final ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)14);
            final ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(AQUA + "Poprzednia");
            itemStack.setItemMeta(itemMeta);
            this.addElement(offset, itemStack, event -> this.drawPage(this.getCurrentPage() - 1));
        }
        else if (this.showBackToCategories)
        {
            final ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)14);
            final ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(AQUA + "Kategorie");
            itemStack.setItemMeta(itemMeta);
            this.addElement(offset, itemStack, event -> {
                this.close();
                this.categoriesAction.accept(this.getPlayer());
            });
        }

        {
            final ItemStack sign = new ItemStack(Material.SIGN, 1);
            final ItemMeta itemMeta = sign.getItemMeta();
            itemMeta.setDisplayName(AQUA + "Strona " + this.getCurrentPage());
            sign.setItemMeta(itemMeta);
            this.addElement(offset + 4, sign);
        }

        if (this.hasNextPage())
        {
            final ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
            final ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(AQUA + "Nastepna");
            itemStack.setItemMeta(itemMeta);
            this.addElement(offset + 8, itemStack, event -> this.drawPage(this.getCurrentPage() + 1));
        }

        {
            final ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
            final ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(" ");
            itemStack.setItemMeta(itemMeta);
            this.fillEmpty(itemStack);
        }
    }

    @Override
    protected Pair<ItemStack, ClickHandler> drawElement(final IShopEntry entry)
    {
        switch (entry.getEntryType())
        {
            case PLACEHOLDER:
                return this.drawPlaceholder(entry);
            case ITEM:
                return this.drawItem(entry);
        }
        throw new RuntimeException(entry.toString());
    }

    private Pair<ItemStack, ClickHandler> drawPlaceholder(final IShopEntry shopEntry)
    {
        final ItemStack placeholder = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15);
        final ItemMeta itemMeta = placeholder.getItemMeta();
        itemMeta.setDisplayName(" ");
        placeholder.setItemMeta(itemMeta);
        return Pair.of(placeholder, event -> {});
    }

    private Pair<ItemStack, ClickHandler> drawItem(final IShopEntry entry)
    {
        final ItemStack itemStack = entry.getBukkitItem().asBukkit();
        final ItemMeta itemMeta = itemStack.getItemMeta();

        final List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
        lore.add(""); // empty line
        if (! this.hasPermission.get()) // gdy gracz nie ma uprawnien do kategorii
        {
            lore.add(RED + "Nie masz uprawnien");
            lore.add(RED + "do tej czesci sklepu");
            lore.add("");

            if (entry.canBuy())
            {
                lore.add(YELLOW + "Kupno: " + GREEN + entry.getBuyPrice());
            }
            else
            {
                lore.add(RED + "Nie mozna kupic");
            }

            if (entry.canSell())
            {
                lore.add(YELLOW + "Sprzedaz: " + GREEN + entry.getSellPrice());
            }
            else
            {
                lore.add(RED + "Nie mozna sprzedac");
            }
        }
        else // gdy ma uprawnienia do kategorii
        {
            if (entry.canBuy())
            {
                lore.add(YELLOW + "Kupno: " + GREEN + entry.getBuyPrice() + YELLOW + " (Kliknij Lewym)");
            }
            else
            {
                lore.add(RED + "Nie mozna kupic");
            }

            if (entry.canSell())
            {
                lore.add(YELLOW + "Sprzedaz: " + GREEN + entry.getSellPrice() + YELLOW + " (Kliknij Prawym)");
            }
            else
            {
                lore.add(RED + "Nie mozna sprzedac");
            }
        }
        itemMeta.setLore(lore);
        if (! StringUtils.isEmpty(entry.getDisplayName()))
        {
            itemMeta.setDisplayName(entry.getDisplayName());
        }
        itemStack.setItemMeta(itemMeta);

        return Pair.of(itemStack, event -> this.shopComponent.getShopManager().processPayment(this.category, entry, event));
    }

    private Boolean checkPermission()
    {
        final String permission = this.category.getPermission();
        return StringUtils.isEmpty(permission) || this.getPlayer().hasPermission(permission);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("showBackToCategories", this.showBackToCategories).toString();
    }
}
