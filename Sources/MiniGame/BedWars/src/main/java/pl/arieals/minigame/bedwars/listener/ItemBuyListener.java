package pl.arieals.minigame.bedwars.listener;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.minigame.bedwars.event.ItemBuyEvent;
import pl.arieals.minigame.bedwars.event.ItemPreBuyEvent;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.LegacyMessage;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;

public class ItemBuyListener implements Listener
{
    @Inject @Messages("BedWarsShop")
    private MessagesBox messagesShop;

    @EventHandler(priority = EventPriority.MONITOR)
    public void itemBuySound(final ItemBuyEvent event)
    {
        final Player player = event.getPlayer();
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2); // volume, pitch
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void announceItemBuy(final ItemBuyEvent event)
    {
        final INorthPlayer player = event.getPlayer();

        final String messageKey = "item." + event.getShopEntry().getInternalName() + ".name";
        final LegacyMessage itemName = this.messagesShop.getLegacy(player.getLocale(), messageKey, "&e");

        player.sendMessage(this.messagesShop, "action.buy_item", itemName);
    }

    @EventHandler
    public void checkIfHaveMoney(final ItemPreBuyEvent event)
    {
        final Player player = event.getPlayer();
        final ItemStack price = event.getPrice();

        if (! player.getInventory().containsAtLeast(price, price.getAmount()))
        {
            event.setBuyStatus(ItemPreBuyEvent.BuyStatus.NOT_ENOUGH_CURRENCY);

            if (! event.isCheck())
            {
                // odtwarzamy dzwiek braku kasy jesli to nie jest sprawdzenie
                player.playSound(player.getLocation(), Sound.ENTITY_ARMORSTAND_BREAK, 0.5f, 2); // volume, pitch // dzwiek braku kasy
            }
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
