package pl.arieals.lobby.chest.opening;

import java.util.Collection;
import java.util.Iterator;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.globalshops.shared.Rarity;
import pl.arieals.lobby.chest.BaseOpeningHoloGui;
import pl.arieals.lobby.chest.loot.ILoot;
import pl.arieals.lobby.chest.loot.ItemShardLoot;
import pl.arieals.lobby.chest.loot.LootResult;
import pl.north93.zgame.api.bukkit.hologui.IHoloContext;
import pl.north93.zgame.api.bukkit.hologui.IIcon;
import pl.north93.zgame.api.bukkit.hologui.IconNameLocation;
import pl.north93.zgame.api.bukkit.hologui.IconPosition;

class OpeningResultHoloGui extends BaseOpeningHoloGui
{
    private final LootResult lootResult;

    OpeningResultHoloGui(final LootResult result)
    {
        this.lootResult = result;
    }

    @Override
    protected void openGui0(final IHoloContext context)
    {
        this.setupLootIcons(context);
        this.playLegendarySound(context);

        this.shopIcon.setPosition(new IconPosition(3, 60, 1.5));
        this.closeIcon.setPosition(new IconPosition(3, -60, 1.5));
    }

    // odtwarza dzwiek legendy
    private void playLegendarySound(final IHoloContext context)
    {
        if (! this.containsLegendaryItem())
        {
            return;
        }

        final Player player = context.getPlayer();
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_GROWL, 1, 1.5f); // volume, pitch
    }

    // sprawdza czy loot w tym gui zawiera legendarny przedmiot
    private boolean containsLegendaryItem()
    {
        final Collection<ILoot> loot = this.lootResult.getLoot();
        for (final ILoot iLoot : loot)
        {
            if (iLoot instanceof ItemShardLoot)
            {
                final Rarity rarity = ((ItemShardLoot) iLoot).getItem().getRarity();
                if (rarity == Rarity.LEGENDARY)
                {
                    return true;
                }
            }
        }
        return false;
    }

    // glowna metoda wejsciowa konfigurujaca ikonki lootu
    private void setupLootIcons(final IHoloContext holoContext)
    {
        final Collection<ILoot> loot = this.lootResult.getLoot();
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

    // tworzy ikone dla konkretnego lootu w konretnej lokalizacji
    private void createIcon(final IHoloContext holoContext, final ILoot loot, final int position, final IconNameLocation nameLocation)
    {
        final IIcon icon = holoContext.createIcon();
        icon.setPosition(new IconPosition(4.5, position, 1.5));
        icon.setNameLocation(nameLocation);
        loot.setupIcon(icon);
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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
