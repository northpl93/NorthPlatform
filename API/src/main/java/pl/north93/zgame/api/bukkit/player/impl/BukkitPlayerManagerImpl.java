package pl.north93.zgame.api.bukkit.player.impl;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.player.IBukkitPlayers;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.network.players.IPlayer;
import pl.north93.zgame.api.global.redis.observable.Value;

public class BukkitPlayerManagerImpl extends Component implements IBukkitPlayers
{
    @Inject
    private BukkitApiCore   bukkitApiCore;
    @Inject
    private INetworkManager networkManager;

    @Override
    protected void enableComponent()
    {
        this.bukkitApiCore.registerEvents(new JoinLeftListener(), new ChatListener(), new LanguageKeeper());
    }

    @Override
    protected void disableComponent()
    {
    }

    @Override
    public OfflinePlayer getBukkitOfflinePlayer(final UUID uuid)
    {
        final IPlayer player = this.networkManager.getPlayers().unsafe().getOffline(uuid);
        if (player == null)
        {
            return null;
        }
        return new NorthOfflinePlayer(player);
    }

    @Override
    public OfflinePlayer getBukkitOfflinePlayer(final String nick)
    {
        final IPlayer player = this.networkManager.getPlayers().unsafe().getOffline(nick);
        if (player == null)
        {
            return null;
        }
        return new NorthOfflinePlayer(player);
    }

    @Override
    public INorthPlayer getPlayer(final Player player)
    {
        final Value<IOnlinePlayer> onlinePlayerData = this.networkManager.getPlayers().unsafe().getOnline(player.getName());
        return this.wrapNorthPlayer(player, onlinePlayerData);
    }

    @Override
    public INorthPlayer getPlayer(final UUID uuid)
    {
        return this.getPlayer(Bukkit.getPlayer(uuid));
    }

    @Override
    public INorthPlayer getPlayer(final String nick)
    {
        return this.getPlayer(Bukkit.getPlayer(nick));
    }

    private INorthPlayer wrapNorthPlayer(final Player player, final Value<IOnlinePlayer> playerData)
    {
        if (player instanceof NorthPlayer)
        {
            return (INorthPlayer) player;
        }
        return new NorthPlayer(this.networkManager, player, playerData);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
