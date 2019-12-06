package pl.arieals.minigame.bedwars.shop.stattrack;

import lombok.ToString;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import pl.arieals.minigame.bedwars.event.ItemBuyEvent;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.minigame.server.gamehost.event.player.PlayerJoinArenaEvent;
import pl.north93.northplatform.globalshops.server.IGlobalShops;
import pl.north93.northplatform.globalshops.server.IPlayerContainer;
import pl.north93.northplatform.globalshops.server.domain.Item;
import pl.north93.northplatform.globalshops.server.domain.ItemsGroup;

import java.util.ArrayList;
import java.util.Locale;

import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getPlayerData;
import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.setPlayerData;

@ToString
public class StatTrackListener implements Listener
{
    @Inject
    private IGlobalShops globalShops;
    @Inject
    private StatTrackManager statTrackManager;
    @Inject
    private StatTrackItems statTrackItems;

    @EventHandler
    public void createStatTrackPlayer(final PlayerJoinArenaEvent event)
    {
        final Player player = event.getPlayer();
        final ItemsGroup bedWarsPerks = this.globalShops.getGroup("bedwars_perks");
        final IPlayerContainer playerContainer = this.globalShops.getPlayer(player);

        final ArrayList<TrackedWeapon> enabledWeapons = new ArrayList<>(4);
        for (final TrackedWeapon trackedWeapon : TrackedWeapon.values())
        {
            final Item item = this.globalShops.getItem(bedWarsPerks, "stattrack_" + trackedWeapon.name().toLowerCase(Locale.ROOT));
            if (! playerContainer.hasBoughtItem(item))
            {
                continue;
            }

            enabledWeapons.add(trackedWeapon);
        }

        final StatTrackPlayer playerData = new StatTrackPlayer(player, enabledWeapons);
        setPlayerData(player, playerData);
        this.statTrackManager.preCacheData(playerData);
    }

    @EventHandler
    public void onItemPickup(final PlayerPickupItemEvent event)
    {
        final ItemStack itemStack = event.getItem().getItemStack();

        final StatTrackPlayer playerData = getPlayerData(event.getPlayer(), StatTrackPlayer.class);
        final TrackedWeapon weapon = TrackedWeapon.getByMaterial(itemStack.getType());
        if (weapon == null || playerData == null || !playerData.isEnabled(weapon))
        {
            return;
        }

        this.statTrackItems.updateItem(playerData, weapon, itemStack);
    }

    @EventHandler
    public void dropItem(final PlayerDropItemEvent event)
    {
        final ItemStack itemStack = event.getItemDrop().getItemStack();

        final StatTrackPlayer playerData = getPlayerData(event.getPlayer(), StatTrackPlayer.class);
        final TrackedWeapon weapon = TrackedWeapon.getByMaterial(itemStack.getType());
        if (weapon == null || playerData == null || !playerData.isEnabled(weapon))
        {
            return;
        }

        this.statTrackItems.clearItem(itemStack);
    }

    @EventHandler
    public void clickItem(final InventoryClickEvent event)
    {
        final Player player = (Player) event.getWhoClicked();

        final StatTrackPlayer playerData = getPlayerData(player, StatTrackPlayer.class);
        if (playerData == null)
        {
            return;
        }

        final ItemStack cursor = event.getCursor();
        final TrackedWeapon cursorWeapon = TrackedWeapon.getByMaterial(cursor.getType());
        if (event.getClickedInventory() != player.getInventory())
        {
            if (cursorWeapon != null)
            {
                this.statTrackItems.clearItem(cursor);
            }
        }
        else
        {
            if (cursorWeapon != null)
            {
                this.statTrackItems.updateItem(playerData, cursorWeapon, cursor);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void applyStatTrackToBoughtItems(final ItemBuyEvent event)
    {
        for (final ItemStack itemStack : event.getItems())
        {
            this.statTrackItems.updateItem(event.getPlayer(), itemStack);
        }
    }
}
