package pl.north93.northplatform.minigame.bedwars.listener;

import static org.bukkit.event.EventPriority.MONITOR;


import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.server.AutoListener;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.minigame.bedwars.cfg.BwShopConfig;
import pl.north93.northplatform.minigame.bedwars.event.UpgradeInstallEvent;
import pl.north93.northplatform.minigame.bedwars.shop.upgrade.IUpgrade;

public class UpgradeInstallListener implements AutoListener
{
    @Inject
    private BwShopConfig config;
    @Inject @Messages("BedWarsShop")
    private MessagesBox messagesShop;

    @EventHandler
    public void onUpgradeInstall(final UpgradeInstallEvent event)
    {
        final IUpgrade upgrade = event.getUpgrade();

        final int currentLevel = event.getTeam().getUpgrades().getUpgradeLevel(upgrade);
        if (currentLevel >= upgrade.maxLevel())
        {
            // jesli mamy juz maksymalny level to anulujemy zakup
            event.setCancelled(true);
            return;
        }

        final Integer upgradePrice = upgrade.getPrice(this.config, event.getTeam());
        if (upgradePrice == null)
        {
            event.setCancelled(true);
            return;
        }

        final PlayerInventory inventory = event.getIssuer().getInventory();
        if (inventory.contains(Material.DIAMOND, upgradePrice))
        {
            if (event.isInstalling())
            {
                inventory.removeItem(new ItemStack(Material.DIAMOND, upgradePrice));
            }
        }
        else
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = MONITOR, ignoreCancelled = true)
    public void announceUpgrade(final UpgradeInstallEvent event)
    {
        if (! event.isInstalling())
        {
            return;
        }

        final Player issuer = event.getIssuer();
        for (final INorthPlayer player : event.getTeam().getBukkitPlayers())
        {
            final String messageKey = "upgrade_gui." + event.getUpgrade().getName() + ".name";
            final String upgradeName = this.messagesShop.getString(player.getLocale(), messageKey, "&e");

            player.sendMessage(this.messagesShop, "action.buy_upgrade", issuer.getDisplayName(), upgradeName);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void upgradeBuySound(final UpgradeInstallEvent event)
    {
        if (! event.isInstalling())
        {
            return;
        }

        for (final Player player : event.getTeam().getBukkitPlayers())
        {
            if (event.isCancelled())
            {
                player.playSound(player.getLocation(), Sound.ENTITY_ARMORSTAND_BREAK, 1, 2); // volume, pitch
            }
            else
            {
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2); // volume, pitch
            }
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
