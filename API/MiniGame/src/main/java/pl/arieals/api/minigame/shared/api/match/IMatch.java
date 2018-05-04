package pl.arieals.api.minigame.shared.api.match;

import java.time.Instant;
import java.util.Collection;
import java.util.UUID;

import pl.arieals.api.minigame.shared.api.GameIdentity;
import pl.arieals.api.minigame.shared.api.statistics.IStatisticHolder;
import pl.north93.zgame.api.global.network.players.Identity;

public interface IMatch
{
    UUID getMatchId();

    UUID getArenaId();

    UUID getServerId();

    GameIdentity getGameIdentity();

    Instant getStartedAt();

    Collection<Identity> getStartParticipants();

    boolean isEnded();

    Instant getEndedAt();

    IStatisticHolder getStatistics();
}
