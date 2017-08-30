package pl.north93.zgame.api.bukkit.player.impl;

import java.util.Locale;

import com.destroystokyo.paper.event.player.PlayerLocaleChangeEvent;

import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.player.IBukkitPlayers;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class LanguageKeeper implements Listener
{
    @Inject
    private IBukkitPlayers bukkitPlayers;

    @EventHandler
    public void onClientSettings(final PlayerLocaleChangeEvent event)
    {
        final INorthPlayer northPlayer = this.bukkitPlayers.getPlayer(event.getPlayer());
        updateLocale(event.getPlayer(), northPlayer.getLocale());
    }

    public static void updateLocale(final Player player, final Locale locale)
    {
        ((CraftPlayer) player).getHandle().locale = locale.toLanguageTag();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
