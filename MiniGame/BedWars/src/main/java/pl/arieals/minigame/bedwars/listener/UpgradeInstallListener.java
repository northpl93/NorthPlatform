package pl.arieals.minigame.bedwars.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import pl.arieals.minigame.bedwars.arena.upgrade.IUpgrade;
import pl.arieals.minigame.bedwars.cfg.BedWarsConfig;
import pl.arieals.minigame.bedwars.event.UpgradeInstallEvent;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class UpgradeInstallListener implements Listener
{
    @Inject
    private BedWarsConfig config;

    @EventHandler
    public void onUpgradeInstall(final UpgradeInstallEvent event)
    {
        final IUpgrade upgrade = event.getUpgrade();
        final Integer upgradePrice = this.config.getUpgrades().get(upgrade.getName());

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
}
