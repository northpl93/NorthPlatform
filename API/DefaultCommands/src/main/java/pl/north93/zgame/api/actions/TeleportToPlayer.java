package pl.north93.zgame.api.actions;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.global.network.server.joinaction.IServerJoinAction;

public class TeleportToPlayer implements IServerJoinAction
{
    private UUID playerId;

    public TeleportToPlayer()
    {
    }

    public TeleportToPlayer(final UUID playerId)
    {
        this.playerId = playerId;
    }

    @Override
    public void playerJoined(final INorthPlayer bukkitPlayer)
    {
        final Player player = Bukkit.getPlayer(this.playerId);
        if (player != null)
        {
            bukkitPlayer.teleport(player.getLocation());
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("playerId", this.playerId).toString();
    }
}
