package pl.arieals.minigame.bedwars.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.minigame.bedwars.event.ItemBuyEvent;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class ItemBuyListener implements Listener
{
    @Inject @Messages("BedWarsShop")
    private MessagesBox messagesShop;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void announceItemBuy(final ItemBuyEvent event)
    {
        final Player player = event.getPlayer();

        final String messageKey = "item." + event.getShopEntry().getInternalName() + ".name";
        final String itemName = this.messagesShop.getMessage(player.spigot().getLocale(), messageKey, "e");

        this.messagesShop.sendMessage(player, "action.buy_item", itemName);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
