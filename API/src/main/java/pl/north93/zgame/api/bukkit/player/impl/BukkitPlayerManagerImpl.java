package pl.north93.zgame.api.bukkit.player.impl;

import java.util.UUID;

import org.bukkit.OfflinePlayer;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.player.IBukkitPlayerManager;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IPlayer;

public class BukkitPlayerManagerImpl extends Component implements IBukkitPlayerManager
{
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager networkManager;

    @Override
    protected void enableComponent()
    {
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
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
