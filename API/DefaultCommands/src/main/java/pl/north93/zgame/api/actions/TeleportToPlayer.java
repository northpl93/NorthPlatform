package pl.north93.zgame.api.actions;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.global.network.server.joinaction.IServerJoinAction;

public class TeleportToPlayer implements IServerJoinAction
{
    private UUID    playerId;
    private Boolean byCommand;

    public TeleportToPlayer()
    {
    }

    public TeleportToPlayer(final UUID playerId, final Boolean byCommand)
    {
        this.playerId = playerId;
        this.byCommand = byCommand;
    }

    @Override
    public void playerJoined(final INorthPlayer bukkitPlayer)
    {
        final Player player = Bukkit.getPlayer(this.playerId);
        if (player != null)
        {
            final TeleportCause cause = this.byCommand ? TeleportCause.COMMAND : TeleportCause.PLUGIN;
            bukkitPlayer.teleport(player.getLocation(), cause);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("playerId", this.playerId).toString();
    }
}
