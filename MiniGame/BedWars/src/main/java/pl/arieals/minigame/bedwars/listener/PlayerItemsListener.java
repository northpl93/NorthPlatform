package pl.arieals.minigame.bedwars.listener;

import net.minecraft.server.v1_10_R1.CombatEntry;
import net.minecraft.server.v1_10_R1.CombatTracker;
import net.minecraft.server.v1_10_R1.Entity;

import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.utils.reflections.DioriteReflectionUtils;
import org.diorite.utils.reflections.MethodInvoker;

import pl.arieals.minigame.bedwars.shop.ShopManager;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class PlayerItemsListener implements Listener
{
    @Inject
    private ShopManager shopManager;

    @EventHandler
    public void onModifyArmor(final InventoryClickEvent event)
    {
        if (event.getSlotType() == InventoryType.SlotType.ARMOR)
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void blockDropPermanentItem(final PlayerDropItemEvent event)
    {
        final ItemStack itemStack = event.getItemDrop().getItemStack();
        if (this.shopManager.isItemPermanent(itemStack))
        {
            event.setCancelled(true);
        }
    }

    /*
     * Zbroja zostaje
     * Surowce do zabojcy
     * Reszta przepada o ile nie trwala.
     */
    @EventHandler
    public void filterDeathItems(final PlayerDeathEvent event)
    {
        event.setKeepInventory(true);

        final Player player = event.getEntity();
        final ItemStack[] storageContents = player.getInventory().getStorageContents();

        final MethodInvoker combatTracker_j = DioriteReflectionUtils.getMethod(CombatTracker.class, "j");
        final CombatEntry lastPlayerDamage = (CombatEntry) combatTracker_j.invoke(((CraftPlayer) player).getHandle().combatTracker);
        final Entity entity = lastPlayerDamage.a().getEntity();
        System.out.println(entity);

        for (int i = 0; i < storageContents.length; i++)
        {
            final ItemStack item = storageContents[i];
            if (this.shopManager.isItemPermanent(item))
            {
                continue;
            }

            player.getInventory().setItem(i, null);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
