package pl.arieals.minigame.bedwars.listener;

import static org.bukkit.event.EventPriority.MONITOR;


import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.minigame.bedwars.arena.Team;
import pl.arieals.minigame.bedwars.cfg.BwConfig;
import pl.arieals.minigame.bedwars.event.UpgradeInstallEvent;
import pl.arieals.minigame.bedwars.shop.upgrade.IUpgrade;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class UpgradeInstallListener implements Listener
{
    @Inject
    private BwConfig    config;
    @Inject @Messages("BedWarsShop")
    private MessagesBox messagesShop;

    @EventHandler
    public void onUpgradeInstall(final UpgradeInstallEvent event)
    {
        final IUpgrade upgrade = event.getUpgrade();
        final Integer upgradePrice = this.getPrice(event.getTeam(), upgrade);
        if (upgradePrice == null)
        {
            event.setCancelled(true);
            return;
        }

        final PlayerInventory inventory = event.getIssuer().getInventory();
        if (inventory.contains(Material.DIAMOND, upgradePrice))
        {
            inventory.removeItem(new ItemStack(Material.DIAMOND, upgradePrice));
        }
        else
        {
            event.setCancelled(true);
        }
    }

    private Integer getPrice(final Team team, final IUpgrade upgrade)
    {
        final Map<String, Integer> upgrades = this.config.getUpgrades();

        if (upgrades.containsKey(upgrade.getName()))
        {
            return upgrades.get(upgrade.getName());
        }

        final int nextLevel = team.getUpgrades().getUpgradeLevel(upgrade) + 1;
        return upgrades.get(upgrade.getName() + "_" + nextLevel);
    }

    @EventHandler(priority = MONITOR, ignoreCancelled = true)
    public void announceUpgrade(final UpgradeInstallEvent event)
    {
        final Player issuer = event.getIssuer();

        for (final Player player : event.getTeam().getPlayers())
        {
            final String messageKey = "upgrade." + event.getUpgrade().getName();
            final String upgradeName = this.messagesShop.getMessage(player.spigot().getLocale(), messageKey, event.getLevel());

            this.messagesShop.sendMessage(player, "action.buy_upgrade", issuer.getDisplayName(), upgradeName);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
