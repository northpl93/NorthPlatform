package pl.arieals.lobby.chest;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import pl.arieals.lobby.chest.opening.ChestOpeningController;
import pl.north93.zgame.api.bukkit.hologui.IHoloContext;
import pl.north93.zgame.api.bukkit.hologui.IHoloGui;
import pl.north93.zgame.api.bukkit.hologui.IIcon;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public abstract class BaseOpeningHoloGui implements IHoloGui
{
    @Inject
    protected ChestOpeningController openingController;

    protected IIcon shopIcon;
    protected IIcon closeIcon;

    @Override
    public final void openGui(final IHoloContext context)
    {
        this.shopIcon = context.createIcon();
        this.shopIcon.setType(new ItemStack(Material.NETHER_STAR));

        this.closeIcon = context.createIcon();
        this.closeIcon.setType(new ItemStack(Material.BARRIER));

        this.openGui0(context);

        context.addIcon(this.shopIcon);
        context.addIcon(this.closeIcon);
    }

    protected abstract void openGui0(final IHoloContext context);

    @Override
    public void closeGui(final IHoloContext context)
    {
    }
}
