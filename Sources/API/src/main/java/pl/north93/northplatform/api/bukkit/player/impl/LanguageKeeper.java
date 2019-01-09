package pl.north93.northplatform.api.bukkit.player.impl;

import java.util.Locale;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLocaleChangeEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.player.IBukkitPlayers;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.utils.AutoListener;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

public class LanguageKeeper implements AutoListener
{
    @Inject
    private IBukkitPlayers bukkitPlayers;

    @EventHandler
    public void onClientSettings(final PlayerLocaleChangeEvent event)
    {
        final INorthPlayer northPlayer = this.bukkitPlayers.getPlayer(event.getPlayer());
        updateLocale(event.getPlayer(), northPlayer.getMyLocale());
    }

    public static void updateLocale(final Player player, final Locale locale)
    {
        INorthPlayer.asCraftPlayer(player).getHandle().locale = locale.toLanguageTag();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
