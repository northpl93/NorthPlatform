package pl.north93.zgame.api.bukkit.listeners;

import java.util.Locale;

import com.destroystokyo.paper.event.player.PlayerLocaleChangeEvent;

import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;

public class LanguageKeeper implements Listener
{
    @EventHandler
    public void onClientSettings(final PlayerLocaleChangeEvent event)
    {
        final IOnlinePlayer player = (IOnlinePlayer) API.getNetworkManager().getPlayers().unsafe().get(event.getPlayer().getName());
        updateLocale(event.getPlayer(), player.getLocale());
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
