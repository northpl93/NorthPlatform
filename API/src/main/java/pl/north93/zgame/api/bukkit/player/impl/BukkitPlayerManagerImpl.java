package pl.north93.zgame.api.bukkit.player.impl;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.player.IBukkitPlayers;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.network.players.IPlayersManager;
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
        this.bukkitApiCore.registerEvents(new NetworkPlayerDataListener(), new ChatListener(), new LanguageKeeper());
    }

    @Override
    protected void disableComponent()
    {
    }

    @Override
    public OfflinePlayer getBukkitOfflinePlayer(final UUID uuid)
    {
        final IPlayersManager.Unsafe unsafe = this.networkManager.getPlayers().unsafe();
        return unsafe.getOffline(uuid).map(NorthOfflinePlayer::new).orElse(null);
    }

    @Override
    public OfflinePlayer getBukkitOfflinePlayer(final String nick)
    {
        final IPlayersManager.Unsafe unsafe = this.networkManager.getPlayers().unsafe();
        return unsafe.getOffline(nick).map(NorthOfflinePlayer::new).orElse(null);
    }

    @Override
    public INorthPlayer getPlayer(final Player player)
    {
        return this.wrapNorthPlayer(player);
    }

    @Override
    public INorthPlayer getPlayer(final NorthCommandSender northCommandSender)
    {
        final Player bukkitPlayer = (Player) northCommandSender.unwrapped();
        return this.wrapNorthPlayer(bukkitPlayer);
    }

    @Override
    public INorthPlayer getPlayer(final UUID uuid)
    {
        return Optional.ofNullable(Bukkit.getPlayer(uuid)).map(this::wrapNorthPlayer).orElse(null);
    }

    @Override
    public INorthPlayer getPlayer(final String nick)
    {
        return Optional.ofNullable(Bukkit.getPlayer(nick)).map(this::wrapNorthPlayer).orElse(null);
    }

    @Override
    public INorthPlayer getPlayerExact(final String exactNick)
    {
        return Optional.ofNullable(Bukkit.getPlayerExact(exactNick)).map(this::wrapNorthPlayer).orElse(null);
    }

    @Override
    public CraftPlayer getCraftPlayer(final Player player)
    {
        if (player instanceof CraftPlayer)
        {
            return (CraftPlayer) player;
        }
        return ((INorthPlayer) player).getCraftPlayer();
    }

    @Override
    public Collection<INorthPlayer> getPlayers()
    {
        return this.getStream().collect(Collectors.toList());
    }

    @Override
    public Stream<INorthPlayer> getStream()
    {
        return Bukkit.getOnlinePlayers().stream().map(this::wrapNorthPlayer);
    }

    private INorthPlayer wrapNorthPlayer(final Player player)
    {
        if (player instanceof NorthPlayerImpl)
        {
            return (INorthPlayer) player;
        }

        final Value<IOnlinePlayer> playerData = this.networkManager.getPlayers().unsafe().getOnlineValue(player.getName());
        return new NorthPlayerImpl(this.networkManager, player, playerData);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
