package pl.arieals.lobby.chest.opening;

import java.util.Collection;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.globalshops.shared.Item;
import pl.arieals.lobby.chest.BaseOpeningHoloGui;
import pl.arieals.lobby.chest.loot.ILoot;
import pl.arieals.lobby.chest.loot.ItemShardLoot;
import pl.arieals.lobby.chest.loot.LootResult;
import pl.arieals.lobby.chest.loot.ShopIconFinder;
import pl.north93.zgame.api.bukkit.hologui.IHoloContext;
import pl.north93.zgame.api.bukkit.hologui.IIcon;
import pl.north93.zgame.api.bukkit.hologui.IconNameLocation;
import pl.north93.zgame.api.bukkit.hologui.IconPosition;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.TranslatableString;

class OpeningResultHoloGui extends BaseOpeningHoloGui
{
    @Inject
    private ShopIconFinder shopIconFinder;

    private final LootResult lootResult;

    OpeningResultHoloGui(final LootResult result)
    {
        this.lootResult = result;
    }

    @Override
    protected void openGui0(final IHoloContext context)
    {
        this.setupLootIcons(context, this.lootResult);

        this.shopIcon.setPosition(new IconPosition(3, 60, 1.5));
        this.closeIcon.setPosition(new IconPosition(3, -60, 1.5));
    }

    private void setupLootIcons(final IHoloContext holoContext, final LootResult result)
    {
        final Collection<ILoot> loot = result.getLoot();
        if (loot.isEmpty())
        {
            return;
        }

        final Iterator<ILoot> iterator = loot.iterator();
        if (loot.size() % 2 != 0) // nieparzysta ilosc, tworzymy ikonke na srodku
        {
            this.createIcon(holoContext, iterator.next(), 0, IconNameLocation.ABOVE);
        }

        for (int i = 30; iterator.hasNext(); i += 30)
        {
            this.createIcon(holoContext, iterator.next(), i, IconNameLocation.BELOW);
            this.createIcon(holoContext, iterator.next(), - i, IconNameLocation.BELOW);
        }
    }

    private void createIcon(final IHoloContext holoContext, final ILoot loot, final int position, final IconNameLocation nameLocation)
    {
        final IIcon icon = holoContext.createIcon();
        icon.setPosition(new IconPosition(4.5, position, 1.5));
        icon.setNameLocation(nameLocation);

        if (loot instanceof ItemShardLoot)
        {
            final ItemShardLoot shardLoot = (ItemShardLoot) loot;
            final Item item = shardLoot.getItem();

            icon.setType(this.shopIconFinder.getItemStack(item));
            icon.setDisplayName(this.getName(item), TranslatableString.constant(shardLoot.getShards() + " odlamkow"));
        }

        holoContext.addIcon(icon);
    }

    @Override
    public void iconClicked(final IHoloContext context, final IIcon icon)
    {
        final Player player = context.getPlayer();
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1); // volume,pitch

        if (icon == this.closeIcon)
        {
            // probujemy dac graczowi kolejna skrzynke, a przy okazji resetujemy gui
            this.openingController.nextChest(context.getPlayer());
        }
    }

    private TranslatableString getName(final Item item)
    {
        final TranslatableString name;
        switch (item.getRarity())
        {
            case NORMAL:
                name = TranslatableString.constant(ChatColor.WHITE.toString());
                break;
            case RARE:
                name = TranslatableString.constant(ChatColor.AQUA.toString());
                break;
            case EPIC:
                name = TranslatableString.constant(ChatColor.LIGHT_PURPLE.toString());
                break;
            case LEGENDARY:
                name = TranslatableString.constant(ChatColor.GOLD.toString());
                break;
            default:
                throw new IllegalArgumentException("Unknown rarity: " + item.getRarity());
        }

        return name.concat(TranslatableString.constant(ChatColor.BOLD.toString())).concat(item.getName());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
