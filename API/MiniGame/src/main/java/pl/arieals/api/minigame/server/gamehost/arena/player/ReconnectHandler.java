package pl.arieals.api.minigame.server.gamehost.arena.player;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import com.google.common.base.Preconditions;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.api.minigame.shared.api.arena.reconnect.IReconnectManager;
import pl.arieals.api.minigame.shared.api.arena.reconnect.ReconnectTicket;
import pl.arieals.api.minigame.shared.api.match.IMatchAccess;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.players.Identity;

/**
 * Wewnętrzna klasa pomocnicza do obsługi powrotu graczy do gry.
 */
/*default*/ class ReconnectHandler
{
    @Inject
    private       IReconnectManager    reconnectManager;
    private final GameHostManager      gameHostManager;
    private final LocalArena           arena;
    private final Set<ReconnectTicket> tickets;

    public ReconnectHandler(final GameHostManager gameHostManager, final LocalArena arena)
    {
        this.gameHostManager = gameHostManager;
        this.arena = arena;

        this.tickets = new HashSet<>();
    }

    public boolean isReconnectSupported()
    {
        return this.gameHostManager.getMiniGameConfig().getReconnect().getEnabled();
    }

    public int getReconnectTime()
    {
        return this.gameHostManager.getMiniGameConfig().getReconnect().getMaxTime();
    }

    public boolean tryReconnect(final ReconnectTicket ticket)
    {
        if (! this.tickets.contains(ticket))
        {
            return false;
        }

        final IMatchAccess match = this.arena.getMatch();
        if (match == null)
        {
            return false;
        }

        return match.getMatchId().equals(ticket.getMatchId());
    }

    public boolean handleReconnect(final Player player)
    {
        final Iterator<ReconnectTicket> iterator = this.tickets.iterator();
        while (iterator.hasNext())
        {
            final ReconnectTicket ticket = iterator.next();
            if (! ticket.getPlayerId().equals(player.getUniqueId()))
            {
                continue;
            }

            iterator.remove();
            return true;
        }

        return false;
    }

    public void addReconnectCandidate(final Player player)
    {
        Preconditions.checkState(this.isReconnectSupported(), "Reconnect is unsupported!");
        Preconditions.checkState(this.arena.getGamePhase() == GamePhase.STARTED, "Player may reconnect only in STARTED gamephase");

        final ReconnectTicket ticket = this.createTicket(player);
        this.tickets.add(ticket);

        this.reconnectManager.updateReconnectTicket(Identity.of(player), ticket);
    }

    public void invalidateReconnect(final Player player)
    {
        final UUID playerId = player.getUniqueId();
        this.tickets.removeIf(ticket -> ticket.getPlayerId().equals(playerId));
    }

    private ReconnectTicket createTicket(final Player player)
    {
        final Instant expiration = Instant.now().plus(this.getReconnectTime(), ChronoUnit.SECONDS);
        final UUID serverId = this.gameHostManager.getServerId();
        final UUID matchId = this.arena.getMatch().getMatchId();

        return new ReconnectTicket(expiration, player.getUniqueId(), serverId, this.arena.getId(), matchId);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("tickets", this.tickets).toString();
    }
}