package pl.arieals.lobby.chest.opening;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.lobby.chest.BaseOpeningHoloGui;
import pl.arieals.lobby.chest.animation.AnimationInstance;
import pl.arieals.lobby.chest.animation.ChestAnimationController;
import pl.arieals.lobby.chest.animation.ChestOpenAnimation;
import pl.north93.zgame.api.bukkit.hologui.IHoloContext;
import pl.north93.zgame.api.bukkit.hologui.IIcon;
import pl.north93.zgame.api.bukkit.hologui.IconPosition;
import pl.north93.zgame.api.bukkit.hologui.hologram.IHologram;
import pl.north93.zgame.api.bukkit.hologui.hologram.PlayerVisibility;
import pl.north93.zgame.api.bukkit.hologui.hologram.impl.HologramFactory;
import pl.north93.zgame.api.bukkit.hologui.hologram.message.LegacyHologramLines;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.TranslatableString;

class OpeningHoloGui extends BaseOpeningHoloGui
{
    @Inject
    private ChestAnimationController animationController;

    private final IOpeningSession openingSession;
    private boolean   isOpening; // blokada przed wielokrotnym kliknieciem
    private IIcon     chestIcon;
    private IHologram hitMeHologram;

    OpeningHoloGui(final IOpeningSession openingSession)
    {
        this.openingSession = openingSession;
    }

    @Override
    protected void openGui0(final IHoloContext context)
    {
        this.shopIcon.setPosition(new IconPosition(3, 25, 1.5));
        this.closeIcon.setPosition(new IconPosition(3, -25, 1.5));

        this.setupChest(context);
        this.setupHologram(context);
    }

    @Override
    public void iconClicked(final IHoloContext context, final IIcon icon)
    {
        final Player player = context.getPlayer();
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1); // volume,pitch

        // obsluga ikon
        if (icon == this.closeIcon)
        {
            this.openingController.closeOpeningGui(player);
        }
        else if (icon == this.chestIcon)
        {
            this.chestClicked(context, icon);
        }
    }

    @Override
    public void closeGui(final IHoloContext context)
    {
        this.animationController.destroyAnimation(context.getPlayer());
        this.removeHologram();
    }

    // czy gracz posiada jakas skrzynke
    private boolean hasChest()
    {
        return this.openingSession.getChestsAmount() > 0;
    }

    private void hideIconsExcludingChest(final IHoloContext context)
    {
        context.removeIcon(this.closeIcon);
        context.removeIcon(this.shopIcon);
        this.removeHologram();
    }

    private void setupChest(final IHoloContext context)
    {
        if (! this.hasChest())
        {
            return;
        }

        final IIcon chestIcon = context.createIcon();
        this.chestIcon = chestIcon;

        chestIcon.setType(new ItemStack(Material.CHEST));
        chestIcon.setPosition(new IconPosition(4, 0, -1));
        chestIcon.setSmall(false);
        context.addIcon(chestIcon);

        this.animationController.createAnimation(context.getPlayer(), chestIcon);
    }

    private void chestClicked(final IHoloContext context, final IIcon icon)
    {
        if (this.isOpening)
        {
            // blokada przed wielokrotnym kliknieciem
            return;
        }
        this.isOpening = true;

        final Player player = context.getPlayer();
        if (! this.openingController.beginChestOpening(player))
        {
            // wymusza odswiezenie widoku jak nie udalo sie rozpoczac otwierania
            this.openingController.nextChest(player);
            return;
        }

        this.hideIconsExcludingChest(context);

        final Location chestLocation = icon.getBackingArmorStand().getLocation();
        // odpalamy dzwiek uderzenia skrzynki
        player.playSound(chestLocation, Sound.BLOCK_WOOD_HIT, 0.4f, 1);
        // uruchamiamy dzwiek elytry, wiatru
        player.playSound(chestLocation, Sound.ITEM_ELYTRA_FLYING, 1, 2);

        final AnimationInstance animationInstance = this.animationController.getInstanceByIcon(icon);
        assert animationInstance != null;

        animationInstance.setAnimation(new ChestOpenAnimation(animationInstance));
    }

    private void setupHologram(final IHoloContext context)
    {
        if (! this.hasChest())
        {
            return;
        }

        final Player player = context.getPlayer();

        final Location location = this.chestIcon.getBackingArmorStand().getLocation();
        location.add(0, 2.7, 0);

        final PlayerVisibility hologramVisibility = new PlayerVisibility(player);

        this.hitMeHologram = HologramFactory.create(hologramVisibility, location);
        this.hitMeHologram.setMessage(new LegacyHologramLines(new TranslatableString[]{TranslatableString.of(this.messages, "@hitme")}));
    }

    private void removeHologram()
    {
        if (this.hitMeHologram != null)
        {
            // usuwamy hologram o uderzeniu skrzynki
            this.hitMeHologram.remove();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

