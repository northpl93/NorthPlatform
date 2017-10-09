package pl.arieals.lobby.chest.opening;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.lobby.chest.BaseOpeningHoloGui;
import pl.arieals.lobby.chest.loot.ILoot;
import pl.arieals.lobby.chest.loot.ItemShardLoot;
import pl.arieals.lobby.chest.loot.LootResult;
import pl.arieals.lobby.chest.loot.ShopIconFinder;
import pl.north93.zgame.api.bukkit.hologui.IHoloContext;
import pl.north93.zgame.api.bukkit.hologui.IIcon;
import pl.north93.zgame.api.bukkit.hologui.IconPosition;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

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

        this.shopIcon.setPosition(new IconPosition(3, 50, 2));
        this.closeIcon.setPosition(new IconPosition(3, -50, 2));
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
            this.createIcon(holoContext, iterator.next(), 0);
        }

        for (int i = 25; iterator.hasNext(); i += 25)
        {
            this.createIcon(holoContext, iterator.next(), i);
            this.createIcon(holoContext, iterator.next(), - i);
        }
    }

    private void createIcon(final IHoloContext holoContext, final ILoot loot, final int position)
    {
        if (loot instanceof ItemShardLoot)
        {
            final ItemShardLoot shardLoot = (ItemShardLoot) loot;

            final IIcon icon = holoContext.createIcon();
            icon.setPosition(new IconPosition(3, position, 2));
            icon.setType(this.shopIconFinder.getItemStack(shardLoot.getItem()));
            icon.setDisplayName(loot.getName());
            holoContext.addIcon(icon);
        }
    }

    @Override
    public void iconClicked(final IHoloContext context, final IIcon icon)
    {
        if (icon == this.closeIcon)
        {
            // probujemy dac graczowi kolejna skrzynke, a przy okazji resetujemy gui
            this.openingController.nextChest(context.getPlayer());
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
