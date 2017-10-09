package pl.arieals.lobby.chest.opening;

import pl.arieals.lobby.chest.BaseOpeningHoloGui;
import pl.north93.zgame.api.bukkit.hologui.IHoloContext;
import pl.north93.zgame.api.bukkit.hologui.IIcon;
import pl.north93.zgame.api.bukkit.hologui.IconPosition;

class OpeningHoloGui extends BaseOpeningHoloGui
{
    @Override
    protected void openGui0(final IHoloContext context)
    {
        this.shopIcon.setPosition(new IconPosition(3, 25, 2));
        this.closeIcon.setPosition(new IconPosition(3, -25, 2));
    }

    @Override
    public void iconClicked(final IHoloContext context, final IIcon icon)
    {

    }
}
