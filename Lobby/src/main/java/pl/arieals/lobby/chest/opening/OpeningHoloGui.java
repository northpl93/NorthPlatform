package pl.arieals.lobby.chest.opening;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import pl.arieals.lobby.chest.BaseOpeningHoloGui;
import pl.north93.zgame.api.bukkit.hologui.IHoloContext;
import pl.north93.zgame.api.bukkit.hologui.IIcon;
import pl.north93.zgame.api.bukkit.hologui.IconPosition;

class OpeningHoloGui extends BaseOpeningHoloGui
{
    @Override
    protected void openGui0(final IHoloContext context)
    {
        this.shopIcon.setPosition(new IconPosition(3, 25, 1.5));
        this.closeIcon.setPosition(new IconPosition(3, -25, 1.5));
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
    }
}
