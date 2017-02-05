package pl.north93.zgame.api.bukkit.player.impl;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.network.players.IPlayer;

public class NorthOfflinePlayer implements OfflinePlayer
{
    private final IPlayer northPlayer;

    public NorthOfflinePlayer(final IPlayer northPlayer)
    {
        this.northPlayer = northPlayer;
    }

    @Override
    public boolean isOnline()
    {
        return this.getPlayer() != null;
    }

    @Override
    public String getName()
    {
        return this.northPlayer.getLatestNick();
    }

    @Override
    public UUID getUniqueId()
    {
        return this.northPlayer.getUuid();
    }

    @Override
    public boolean isBanned()
    {
        return this.northPlayer.isBanned();
    }

    @Override
    public void setBanned(final boolean b)
    {
        throw new UnsupportedOperationException("NorthOfflinePlayer is immutable.");
    }

    @Override
    public boolean isWhitelisted()
    {
        return true;
    }

    @Override
    public void setWhitelisted(final boolean b)
    {
        throw new UnsupportedOperationException("NorthOfflinePlayer is immutable.");
    }

    @Override
    public Player getPlayer()
    {
        return Bukkit.getPlayer(this.getUniqueId());
    }

    @Override
    public long getFirstPlayed()
    {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public long getLastPlayed()
    {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public boolean hasPlayedBefore()
    {
        return true;
    }

    @Override
    public Location getBedSpawnLocation()
    {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Map<String, Object> serialize()
    {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public boolean isOp()
    {
        return false;
    }

    @Override
    public void setOp(final boolean b)
    {
        throw new UnsupportedOperationException("NorthOfflinePlayer is immutable.");
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("northPlayer", this.northPlayer).toString();
    }
}
