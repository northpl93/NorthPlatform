package pl.north93.northplatform.auth.server;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.server.AutoListener;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.MessageLayout;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.network.INetworkManager;
import pl.north93.northplatform.api.global.network.players.Identity;
import pl.north93.northplatform.auth.api.IAuthManager;
import pl.north93.northplatform.auth.api.IAuthPlayer;

public class PlayerListeners implements AutoListener
{
    private final List<String>    allowedCommands;
    @Inject
    private       INetworkManager networkManager;
    @Inject @Messages("NoPremiumAuth")
    private       MessagesBox     messages;
    private       IAuthManager    authManager;

    private PlayerListeners(final IAuthManager authManager)
    {
        this.allowedCommands = Arrays.asList("/login", "/zaloguj", "/l", "/register", "/zarejestruj");
        this.authManager = authManager;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(final PlayerJoinEvent event)
    {
        final INorthPlayer player = INorthPlayer.wrap(event.getPlayer());
        if (this.authManager.isLoggedIn(player.getName()))
        {
            return;
        }

        final IAuthPlayer authPlayer = this.authManager.getPlayer(Identity.of(player));

        this.sendMessage(player, "separator");
        if (authPlayer.isRegistered())
        {
            player.sendMessage(this.messages, "join.login", MessageLayout.CENTER);
        }
        else
        {
            player.sendMessage(this.messages, "join.register", MessageLayout.CENTER);
        }
        this.sendMessage(player, "separator");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(final AsyncPlayerChatEvent event)
    {
        final Player player = event.getPlayer();
        if (! this.authManager.isLoggedIn(player.getName()))
        {
            this.sendMessage(event.getPlayer(), "error.first_login_or_register");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCommand(final PlayerCommandPreprocessEvent event)
    {
        if (this.authManager.isLoggedIn(event.getPlayer().getName()))
        {
            return;
        }

        final String cmd = StringUtils.split(event.getMessage(), ' ')[0];
        if (this.allowedCommands.contains(cmd))
        {
            return;
        }

        event.setCancelled(true);
        this.sendMessage(event.getPlayer(), "error.first_login_or_register");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onMove(final PlayerMoveEvent event)
    {
        final Location from = event.getFrom();
        final Location to = event.getTo();

        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ())
        {
            return;
        }

        final Player player = event.getPlayer();
        if (this.authManager.isLoggedIn(player.getName()))
        {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInventoryClick(final InventoryClickEvent event)
    {
        final HumanEntity player = event.getWhoClicked();
        if (! this.authManager.isLoggedIn(player.getName()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onItemDrop(final PlayerDropItemEvent event)
    {
        final Player player = event.getPlayer();
        if (! this.authManager.isLoggedIn(player.getName()))
        {
            this.sendMessage(event.getPlayer(), "error.first_login_or_register");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onHeldItemChange(final PlayerItemHeldEvent event)
    {
        final Player player = event.getPlayer();
        if (! this.authManager.isLoggedIn(player.getName()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEat(final PlayerItemConsumeEvent event)
    {
        final Player player = event.getPlayer();
        if (! this.authManager.isLoggedIn(player.getName()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteract(final PlayerInteractEvent event)
    {
        final Player player = event.getPlayer();
        if (! this.authManager.isLoggedIn(player.getName()))
        {
            this.sendMessage(event.getPlayer(), "error.first_login_or_register");
            event.setCancelled(true);
        }
    }

    // metoda pomocnicza do wysyłania wiadomości
    private void sendMessage(final Player player, final String message, final Object... args)
    {
        player.sendMessage(this.messages.getComponent(player.getLocale(), message, args));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
