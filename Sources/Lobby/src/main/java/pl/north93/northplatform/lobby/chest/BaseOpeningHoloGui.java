package pl.north93.northplatform.lobby.chest;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import pl.north93.northplatform.lobby.chest.opening.ChestOpeningController;
import pl.north93.northplatform.api.bukkit.hologui.IHoloContext;
import pl.north93.northplatform.api.bukkit.hologui.IHoloGui;
import pl.north93.northplatform.api.bukkit.hologui.IIcon;
import pl.north93.northplatform.api.bukkit.hologui.IconNameLocation;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.messages.TranslatableString;

public abstract class BaseOpeningHoloGui implements IHoloGui
{
    @Inject
    protected ChestOpeningController openingController;
    @Inject @Messages("ChestOpening")
    protected MessagesBox            messages;

    protected IIcon shopIcon;
    protected IIcon closeIcon;

    @Override
    public final void openGui(final IHoloContext context)
    {
        this.shopIcon = context.createIcon();
        this.shopIcon.setType(new ItemStack(Material.NETHER_STAR));
        this.shopIcon.setNameLocation(IconNameLocation.ABOVE);
        this.shopIcon.setDisplayName(TranslatableString.of(this.messages, "@shop.item"));

        this.closeIcon = context.createIcon();
        this.closeIcon.setType(new ItemStack(Material.BARRIER));
        this.closeIcon.setNameLocation(IconNameLocation.ABOVE);
        this.closeIcon.setDisplayName(TranslatableString.of(this.messages, "@back"));

        this.openGui0(context);

        context.addIcon(this.shopIcon);
        context.addIcon(this.closeIcon);
    }

    protected abstract void openGui0(final IHoloContext context);

    @Override
    public void iconClicked(final IHoloContext context, final IIcon icon)
    {
        final Player player = context.getPlayer();
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1); // volume,pitch

        if (icon == this.shopIcon)
        {
            final BaseComponent shopMessage = this.messages.getComponent(player.getLocale(), "shop.click");
            shopMessage.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://mcpiraci.pl/shop"));

            player.sendMessage(shopMessage);
        }
    }

    @Override
    public void closeGui(final IHoloContext context)
    {
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
