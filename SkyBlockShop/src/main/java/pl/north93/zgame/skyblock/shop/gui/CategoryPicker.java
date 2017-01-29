package pl.north93.zgame.skyblock.shop.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.windows.ClickInfo;
import pl.north93.zgame.api.bukkit.windows.Window;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.skyblock.shop.ShopComponent;
import pl.north93.zgame.skyblock.shop.api.ICategory;

public class CategoryPicker extends Window
{
    @InjectComponent("SkyBlock.Shop.Server")
    private ShopComponent shopComponent;
    private final Collection<ICategory> categories;

    public CategoryPicker(final Collection<ICategory> categories)
    {
        super("&aWybierz kategorię", ((categories.size() / 9) + 1) * 9);
        this.categories = categories;
    }

    @Override
    protected void onShow()
    {
        final Player player = this.getPlayer();
        int id = 0;
        for (final ICategory category : this.categories)
        {
            final String permission = category.getPermission();
            final ItemStack representingItem = category.getRepresentingItem().asBukkit();

            final ItemMeta itemMeta = representingItem.getItemMeta();
            itemMeta.setDisplayName(category.getDisplayName());
            if (! StringUtils.isEmpty(permission) && !player.hasPermission(permission))
            {
                final List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
                lore.add(0, ChatColor.RED + "Brak uprawnien!");
            }
            representingItem.setItemMeta(itemMeta);

            this.addElement(id++, representingItem, event -> this.handleOpen(event, category));
        }
    }

    private void handleOpen(final ClickInfo clickInfo, final ICategory category)
    {
        final Player player = clickInfo.getWindow().getPlayer();
        final String permission = category.getPermission();
        if (! StringUtils.isEmpty(permission) && !player.hasPermission(permission))
        {
            player.sendMessage("Nie masz uprawnien do tej kategorii.");
            return;
        }
        this.close();
        this.shopComponent.getShopManager().openCategory(category, this.getPlayer());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("categories", this.categories).toString();
    }
}
