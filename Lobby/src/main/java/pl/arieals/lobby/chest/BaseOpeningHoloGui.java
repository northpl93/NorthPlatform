package pl.arieals.lobby.chest;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import pl.arieals.lobby.chest.opening.ChestOpeningController;
import pl.north93.zgame.api.bukkit.hologui.IHoloContext;
import pl.north93.zgame.api.bukkit.hologui.IHoloGui;
import pl.north93.zgame.api.bukkit.hologui.IIcon;
import pl.north93.zgame.api.bukkit.hologui.IconNameLocation;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.TranslatableString;

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
        this.shopIcon.setNameLocation(IconNameLocation.ABOVE);
        this.shopIcon.setDisplayName(TranslatableString.constant(ChatColor.YELLOW + "Sklep"));

        this.closeIcon = context.createIcon();
        this.closeIcon.setType(new ItemStack(Material.BARRIER));
        this.closeIcon.setNameLocation(IconNameLocation.ABOVE);
        this.closeIcon.setDisplayName(TranslatableString.constant(ChatColor.RED + "Powrót"));

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
