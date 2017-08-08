package pl.north93.zgame.skyplayerexp.server.compass;

import static org.bukkit.ChatColor.translateAlternateColorCodes;


import net.minecraft.server.v1_10_R1.Packet;
import net.minecraft.server.v1_10_R1.PacketPlayInBlockPlace;
import net.minecraft.server.v1_10_R1.PacketPlayInClientCommand;
import net.minecraft.server.v1_10_R1.PacketPlayOutSetSlot;
import net.minecraft.server.v1_10_R1.PlayerConnection;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northspigot.event.PluginItemHeldEvent;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.packets.event.AsyncPacketInEvent;
import pl.north93.zgame.api.bukkit.packets.event.AsyncPacketOutEvent;
import pl.north93.zgame.api.bukkit.packets.wrappers.WrapperPlayInClientCommand;
import pl.north93.zgame.api.bukkit.packets.wrappers.WrapperPlayOutSetSlot;
import pl.north93.zgame.api.global.component.annotations.PostInject;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.skyplayerexp.server.ExperienceServer;

public class CompassManager implements Listener, ICompassManager
{
    private static final ItemStack COMPASS;
    private BukkitApiCore    apiCore;
    @Inject
    private ExperienceServer experience;
    @Inject
    private CompassConnector compassConnector = new CompassConnector();

    @PostInject
    private void postInject()
    {
        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(this, this.apiCore.getPluginMain());
        pluginManager.registerEvents(this.compassConnector, this.apiCore.getPluginMain());
    }

    static
    {
        COMPASS = new ItemStack(Material.COMPASS);
        final ItemMeta itemMeta = COMPASS.getItemMeta();
        itemMeta.setDisplayName(translateAlternateColorCodes('&', "&6MENU SERWERA &7(aby wylaczyc wpisz &6/k wylacz&7)"));
        COMPASS.setItemMeta(itemMeta);
    }

    @EventHandler
    public void compassInteract(final PlayerInteractEvent event)
    {
        final Player player = event.getPlayer();
        final CompassData compassData = this.getCompassData(player);
        if (! compassData.isEnabled())
        {
            return;
        }

        final Action action = event.getAction();
        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)
        {
            player.launchProjectile(EnderPearl.class);
            event.setCancelled(true);
        }
        else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
        {
            this.experience.getServerGuiManager().openServerMenu(player);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void fixCompassInventory(final AsyncPacketOutEvent event)
    {
        final Player player = event.getPlayer();
        final Packet packet = event.getPacket();

        if (packet instanceof PacketPlayOutSetSlot)
        {
            final WrapperPlayOutSetSlot wrapper = new WrapperPlayOutSetSlot((PacketPlayOutSetSlot) packet);
            final int windowId = wrapper.getWindowId();
            if (windowId == -2)
            {
                return;
            }

            final int slotId = wrapper.getSlotId() - 36;
            final CompassData compassData = this.getCompassData(player);
            if (! compassData.isEnabled() || ! compassData.isShow() || compassData.getCurrentSlot() != slotId)
            {
                return;
            }

            final net.minecraft.server.v1_10_R1.ItemStack itemStack = wrapper.getItemStack();
            if (itemStack != null && itemStack.getItem().getName().equals("compass"))
            {
                return;
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void openListener(final AsyncPacketInEvent event)
    {
        final Player player = event.getPlayer();
        final Packet packet = event.getPacket();

        if (packet instanceof PacketPlayInBlockPlace)
        {
            if (this.getCompassData(player).isShow())
            {
                event.setCancelled(true);
                this.apiCore.sync(() -> this.experience.getServerGuiManager().openServerMenu(player));
            }
        }
        else if (packet instanceof PacketPlayInClientCommand)
        {
            final WrapperPlayInClientCommand wrapper = new WrapperPlayInClientCommand((PacketPlayInClientCommand) packet);
            if (wrapper.getClientCommand() == PacketPlayInClientCommand.EnumClientCommand.OPEN_INVENTORY_ACHIEVEMENT)
            {
                this.setCompassShow(player, this.getCompassData(player), false);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void compassGameModeChange(final PlayerGameModeChangeEvent event)
    {
        if (event.getNewGameMode().equals(GameMode.SURVIVAL))
        {
            return;
        }

        this.switchCompassState(event.getPlayer(), false);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void compassOnHeldItem(final PlayerItemHeldEvent event)
    {
        this.updateHeldSlot(event.getPlayer(), event.getPreviousSlot(), event.getNewSlot());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void compassOnPluginHeldItem(final PluginItemHeldEvent event)
    {
        this.updateHeldSlot(event.getPlayer(), event.getPrevious(), event.getCurrent());
    }

    private void updateHeldSlot(final Player player, final int before, final int after)
    {
        final CompassData compassData = this.getCompassData(player);
        if (! compassData.isEnabled())
        {
            return;
        }

        compassData.setCurrentSlot(after);

        this.updateItem(player, before, player.getInventory().getItem(before));
        this.updateItem(player, after, COMPASS);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void compassWindowOpen(final InventoryOpenEvent event)
    {
        final Player player = (Player) event.getPlayer();
        final CompassData compassData = this.getCompassData(player);
        if (! compassData.isEnabled())
        {
            return;
        }

        this.setCompassShow(player, compassData, false);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void compassWindowClose(final InventoryCloseEvent event)
    {
        final Player player = (Player) event.getPlayer();
        final CompassData compassData = this.getCompassData(player);
        if (! compassData.isEnabled())
        {
            return;
        }
        this.setCompassShow(player, compassData, true);
    }

    @Override
    public void switchCompassState(final Player player, final boolean enabled)
    {
        final CompassData compassData = this.getCompassData(player);
        if (compassData.isEnabled() && !enabled)
        {
            // disable
            compassData.setEnabled(false);
            this.setCompassShow(player, compassData, false);
        }
        else if (!compassData.isEnabled() && enabled)
        {
            // enable
            compassData.setEnabled(true);
            this.setCompassShow(player, compassData, true);
        }
    }

    @Override
    public CompassConnector getCompassConnector()
    {
        return this.compassConnector;
    }

    private void setCompassShow(final Player player, final CompassData compassData, final boolean show)
    {
        if (compassData.isShow() && !show)
        {
            // hide
            compassData.setShow(false);
            this.updateItem(player, compassData.getCurrentSlot(), player.getInventory().getItem(compassData.getCurrentSlot()));
        }
        else if (!compassData.isShow() && show)
        {
            // show
            compassData.setCurrentSlot(player.getInventory().getHeldItemSlot());
            compassData.setShow(true);
            this.updateItem(player, compassData.getCurrentSlot(), COMPASS);
        }
    }

    private void updateItem(final Player player, final int slot, final ItemStack itemStack)
    {
        final CraftPlayer craftPlayer = (CraftPlayer) player;
        final PlayerConnection connection = craftPlayer.getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutSetSlot(-2, slot, CraftItemStack.asNMSCopy(itemStack)));
    }

    private CompassData getCompassData(final Player player)
    {
        Object compassData = player.getCompassData();
        if (compassData == null)
        {
            compassData = new CompassData();
            player.setCompassData(compassData);
        }
        return (CompassData) compassData;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
