package pl.north93.zgame.api.bukkit.windows;

import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@SuppressWarnings("SuspiciousMethodCalls")
public class WindowManager implements Listener
{
    private final Map<Player, Window> openedWindows = new WeakHashMap<>();

    public void openWindow(final Player player, final Window window)
    {
        window.setOpened(player);
        this.openedWindows.put(player, window);
    }

    @EventHandler
    public void onClose(final InventoryCloseEvent event)
    {
        this.openedWindows.remove(event.getPlayer());
    }

    @EventHandler
    public void onClick(final InventoryClickEvent event)
    {
        final Window window = this.openedWindows.get(event.getWhoClicked());
        if (event.getClickedInventory() == null || event.getClickedInventory().getType() != InventoryType.CHEST || window == null) // wtf bukkit? How getClickedInventory can be null
        {
            return;
        }
        event.setCancelled(true);
        window.handleClick(event.getSlot());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("openedWindows", this.openedWindows).toString();
    }
}
