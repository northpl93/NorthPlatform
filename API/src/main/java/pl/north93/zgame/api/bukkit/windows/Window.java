package pl.north93.zgame.api.bukkit.windows;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.md_5.bungee.api.ChatColor;

public abstract class Window
{
    private final Int2ObjectMap<ClickHandler> listeners;
    private Inventory inventory;
    private boolean   isOpened;
    private Player    holder;
    private int       size;
    private String    title;

    public Window(final String title, final int size)
    {
        this.title = ChatColor.translateAlternateColorCodes('&', title);
        this.size = size;
        this.listeners = new Int2ObjectOpenHashMap<>();
    }

    public final void close()
    {
        this.holder.closeInventory();
    }

    public final Player getPlayer()
    {
        return this.holder;
    }

    protected final void addElement(final int slot, final ItemStack itemStack)
    {
        this.inventory.setItem(slot, itemStack);
    }

    protected final void setListener(final int slot, final ClickHandler handler)
    {
        this.listeners.put(slot, handler);
    }

    protected final void addElement(final int slot, final ItemStack itemStack, final ClickHandler handler)
    {
        this.addElement(slot, itemStack);
        this.setListener(slot, handler);
    }

    protected final void clear()
    {
        this.listeners.clear();
        this.inventory.clear();
    }

    protected final void fillEmpty(final ItemStack itemStack)
    {
        for (int i = 0; i < this.size; i++)
        {
            if (this.inventory.getItem(i) != null)
            {
                continue;
            }

            this.inventory.setItem(i, itemStack.clone());
        }
    }

    final void setOpened(final Player player)
    {
        if (this.isOpened)
        {
            throw new IllegalStateException("Window already opened.");
        }
        this.isOpened = true;
        this.holder = player;
        this.inventory = Bukkit.createInventory(player, this.size, this.title);
        this.onShow();
        player.openInventory(this.inventory);
    }

    final void handleClick(final ClickInfo clickInfo)
    {
        final ClickHandler handler = this.listeners.get(clickInfo.getSlotId());
        if (handler != null)
        {
            handler.handle(clickInfo);
        }
    }

    final void setClosed()
    {
        this.onClose();
    }

    protected abstract void onShow();

    protected void onClose()
    {
    }

    // utils

    protected static String color(final String message)
    {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', message);
    }

    protected static List<String> lore(final String... message)
    {
        return Arrays.stream(message).map(Window::color).collect(Collectors.toList());
    }
}
